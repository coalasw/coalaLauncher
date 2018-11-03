package org.jackhuang.hmcl.ui.download;

import javafx.application.Platform;
import javafx.scene.Node;
import org.jackhuang.hmcl.download.DownloadProvider;
import org.jackhuang.hmcl.game.ModpackHelper;
import org.jackhuang.hmcl.mod.Modpack;
import org.jackhuang.hmcl.setting.Profile;
import org.jackhuang.hmcl.setting.Profiles;
import org.jackhuang.hmcl.task.Schedulers;
import org.jackhuang.hmcl.task.Task;
import org.jackhuang.hmcl.ui.wizard.WizardController;
import org.jackhuang.hmcl.ui.wizard.WizardProvider;

import java.io.File;
import java.util.Map;

import static org.jackhuang.hmcl.util.Lang.tryCast;
import static org.jackhuang.hmcl.util.i18n.I18n.i18n;

public class ModpackInstallWizardProvider implements WizardProvider {
    private final Profile profile;
    private final File file;

    public ModpackInstallWizardProvider(Profile profile) {
        this(profile, null);
    }

    public ModpackInstallWizardProvider(Profile profile, File modpackFile) {
        this.profile = profile;
        this.file = modpackFile;
    }

    @Override
    public void start(Map<String, Object> settings) {
        if (file != null)
            settings.put(ModpackPage.MODPACK_FILE, file);
        settings.put(PROFILE, profile);
    }

    private Task finishModpackInstallingAsync(Map<String, Object> settings) {
        if (!settings.containsKey(ModpackPage.MODPACK_FILE))
            return null;

        File selected = tryCast(settings.get(ModpackPage.MODPACK_FILE), File.class).orElse(null);
        Modpack modpack = tryCast(settings.get(ModpackPage.MODPACK_MANIFEST), Modpack.class).orElse(null);
        String name = tryCast(settings.get(ModpackPage.MODPACK_NAME), String.class).orElse(null);
        if (selected == null || modpack == null || name == null) return null;

        return ModpackHelper.getInstallTask(profile, selected, name, modpack)
                .then(Task.of(Schedulers.javafx(), () -> profile.setSelectedVersion(name)));
    }

    @Override
    public Object finish(Map<String, Object> settings) {
        settings.put("success_message", i18n("install.success"));
        settings.put("failure_message", i18n("install.failed"));

        return finishModpackInstallingAsync(settings);
    }

    @Override
    public Node createPage(WizardController controller, int step, Map<String, Object> settings) {
        switch (step) {
            case 0:
                return new ModpackPage(controller);
            default:
                throw new IllegalStateException("error step " + step + ", settings: " + settings + ", pages: " + controller.getPages());
        }
    }

    @Override
    public boolean cancel() {
        return true;
    }

    public static final String PROFILE = "PROFILE";
}
