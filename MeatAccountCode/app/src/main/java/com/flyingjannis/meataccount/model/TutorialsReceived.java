package com.flyingjannis.meataccount.model;

public class TutorialsReceived {
    protected boolean createAmountTutorialReceived = false;
    protected boolean mainTutorialReceived = false;
    protected boolean mainTutorialFirstMeatReceived = false;
    protected boolean settingsTutorialReceived = false;

    public void resetAllInformation() {
        createAmountTutorialReceived = false;
        mainTutorialReceived = false;
        mainTutorialFirstMeatReceived = false;
        settingsTutorialReceived = false;
    }

    public void setAllDone() {
        createAmountTutorialReceived = true;
        mainTutorialReceived = true;
        mainTutorialFirstMeatReceived = true;
        settingsTutorialReceived = true;
    }

    public boolean isCreateAmountTutorialReceived() {
        return createAmountTutorialReceived;
    }

    public void setCreateAmountTutorialReceived(boolean createAmountTutorialReceived) {
        this.createAmountTutorialReceived = createAmountTutorialReceived;
    }

    public boolean isMainTutorialReceived() {
        return mainTutorialReceived;
    }

    public void setMainTutorialReceived(boolean mainTutorialReceived) {
        this.mainTutorialReceived = mainTutorialReceived;
    }

    public boolean isMainTutorialFirstMeatReceived() {
        return mainTutorialFirstMeatReceived;
    }

    public void setMainTutorialFirstMeatReceived(boolean mainTutorialFirstMeatReceived) {
        this.mainTutorialFirstMeatReceived = mainTutorialFirstMeatReceived;
    }

    public boolean isSettingsTutorialReceived() {
        return settingsTutorialReceived;
    }

    public void setSettingsTutorialReceived(boolean settingsTutorialReceived) {
        this.settingsTutorialReceived = settingsTutorialReceived;
    }
}
