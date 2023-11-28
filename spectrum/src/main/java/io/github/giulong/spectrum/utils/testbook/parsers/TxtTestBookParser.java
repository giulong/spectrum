package io.github.giulong.spectrum.utils.testbook.parsers;

public class TxtTestBookParser extends SimpleTestBookParser {

    @Override
    public String getRegex() {
        return "^(?<className>[^:#]+?)::(?<testName>[^:#]+?)(##(?<weight>\\d+))?$";
    }
}
