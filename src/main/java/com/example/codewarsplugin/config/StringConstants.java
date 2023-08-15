package com.example.codewarsplugin.config;

public class StringConstants {
    public static final String RECORD_URL = "https://www.codewars.com/api/v1/code-challenges";
    public static final String DASHBOARD_URL = "https://www.codewars.com/dashboard";
    public static final String SIGN_IN_URL = "https://www.codewars.com/users/sign_in";
    public static final String SETUP_URL = "https://www.codewars.com/trainer/setup";
    public static final String AUTHORIZE_URL = "https://www.codewars.com/api/v1/runner/authorize";
    public static final String KATA_URL = "https://www.codewars.com/kata";
    public static final String BASE_URL = "https://www.codewars.com";

    public static String MESSAGE_ICON = "/icons/new_cw_logo.svg";
    public static final String FILENAME = "test.md";
    public static final String BROWSER = "Browser";
    public static final String WORKSPACE = "Workspace";
    public static final String ABOUT = "About";
    public static final String INIT_PY = "__init__.py";
    public static final String PICK_MODULE = "Pick %s Module!";
    public static final String SEVERAL_MODULES = "Project contains several %s modules. Pick the one you want to add kata files to!";
    public static final String PICK_SOURCE = "Pick The Source Directory!";
    public static final String SEVERAL_SOURCES = "Module contains several source directories. Pick the one you want to add kata files to!";
    public static final String MODULE_NOT_FOUND = "{0} module not found in the current project! To setup Kata in {0} start a new {0} project or create a {0} module in the current project! Also, make sure that {0} is supported by IDE and {0} plugin is installed!";
    public static final String JS_MODULE_NOT_FOUND = "Web/JavaScript module not found in the current project! To setup Kata in JS start a new JS/Web project or create a JS module in the current project! JS is supported only by WebStorm and Intellij Ultimate.";
    public static final String KOTLIN_MODULE_NOT_FOUND = "Kotlin/Java module not found in the current project! To setup Kata in Kotlin start a new Java/Kotlin project or create a Java/Kotlin module in the current project!";
    public static final String GROOVY_MODULE_NOT_FOUND = "Groovy/Java module not found in the current project! To setup Kata in Groovy start a new Java/Groovy project or create a Java/Groovy module in the current project!";
    public static final String PYTHON_TEST_FRAMEWORK_MESSAGE = "# codewars_test can be installed from https://github.com/codewars/python-test-framework\n" +
            "# run \"pip install git+https://github.com/codewars/python-test-framework.git#egg=codewars_test\"\n\n\n";
    public static final String RUBY_TEST_FRAMEWORK_MESSAGE = "# To run tests locally a test framework has to be installed manually.\n# Newer katas use RSpec framework while older katas require Codewars Test Framework: \"https://github.com/codewars/ruby-test-framework\"\n# It is not required to install test frameworks.\n# You can modify these tests and submit them to the codewars.com server in the \"workspace\" tab.\n\n";

}
