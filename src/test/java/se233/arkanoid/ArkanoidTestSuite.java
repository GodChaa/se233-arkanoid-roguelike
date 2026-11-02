package se233.arkanoid;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({
        "se233.arkanoid.controller",
        "se233.arkanoid.model",
        "se233.arkanoid.view"
})
public class ArkanoidTestSuite {
}
