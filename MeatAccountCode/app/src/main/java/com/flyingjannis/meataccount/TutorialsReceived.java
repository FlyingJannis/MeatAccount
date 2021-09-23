package com.flyingjannis.meataccount;

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
}
