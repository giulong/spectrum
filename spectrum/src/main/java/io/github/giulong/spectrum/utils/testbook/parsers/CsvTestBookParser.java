package io.github.giulong.spectrum.utils.testbook.parsers;

public class CsvTestBookParser extends SimpleTestBookParser {

    @Override
    public String getRegex() {
        return "^(?<className>[^,]+),(?<testName>[^,]+)(,(?<weight>\\d+))?$";
    }
}
