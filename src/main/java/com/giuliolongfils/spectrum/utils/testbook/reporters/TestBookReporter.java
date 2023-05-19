package com.giuliolongfils.spectrum.utils.testbook.reporters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.giuliolongfils.spectrum.pojos.testbook.TestBook;
import com.giuliolongfils.spectrum.pojos.testbook.TestBookStatistics;
import com.giuliolongfils.spectrum.utils.FileReader;
import lombok.Getter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static java.util.Locale.US;

@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LogTestBookReporter.class, name = "log"),
        @JsonSubTypes.Type(value = HtmlTestBookReporter.class, name = "html"),
})
@Getter
public abstract class TestBookReporter {

    public static final FileReader FILE_READER = FileReader.getInstance();
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(US));

    public abstract void updateWith(TestBook testBook);

    public abstract String getTestsReplacementFrom(TestBook testBook);

    public abstract String getUnmappedTestsReplacementFrom(TestBook testBook);

    public String parse(final String template, final TestBook testBook) {
        final TestBookStatistics statistics = testBook.getStatistics();
        final TestBookStatistics.Percentages percentages = statistics.getPercentages();

        return FILE_READER
                .read(template)
                .replace("{{timestamp}}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .replace("{{tests}}", getTestsReplacementFrom(testBook))
                .replace("{{unmapped-tests}}", getUnmappedTestsReplacementFrom(testBook))
                .replace("{{grand-total}}", statistics.getGrandTotal().toString())
                .replace("{{tests-count}}", String.valueOf(testBook.getTests().size()))
                .replace("{{unmapped-tests-count}}", String.valueOf(testBook.getUnmappedTests().size()))
                .replace("{{successful}}", statistics.getSuccessful().toString())
                .replace("{{failed}}", statistics.getFailed().toString())
                .replace("{{aborted}}", statistics.getAborted().toString())
                .replace("{{disabled}}", statistics.getDisabled().toString())
                .replace("{{not-run}}", statistics.getNotRun().toString())
                .replace("{{grand-total-successful}}", statistics.getGrandTotalSuccessful().toString())
                .replace("{{grand-total-failed}}", statistics.getGrandTotalFailed().toString())
                .replace("{{grand-total-aborted}}", statistics.getGrandTotalAborted().toString())
                .replace("{{grand-total-disabled}}", statistics.getGrandTotalDisabled().toString())
                .replace("{{grand-total-not-run}}", statistics.getGrandTotalNotRun().toString())
                .replace("{{percentages.tests}}", DECIMAL_FORMAT.format(percentages.getSuccessful()))
                .replace("{{percentages.unmapped-tests}}", DECIMAL_FORMAT.format(percentages.getFailed()))
                .replace("{{percentages.successful}}", DECIMAL_FORMAT.format(percentages.getSuccessful()))
                .replace("{{percentages.failed}}", DECIMAL_FORMAT.format(percentages.getFailed()))
                .replace("{{percentages.aborted}}", DECIMAL_FORMAT.format(percentages.getAborted()))
                .replace("{{percentages.disabled}}", DECIMAL_FORMAT.format(percentages.getDisabled()))
                .replace("{{percentages.not-run}}", DECIMAL_FORMAT.format(percentages.getNotRun()))
                .replace("{{percentages.grand-total-successful}}", DECIMAL_FORMAT.format(percentages.getGrandTotalSuccessful()))
                .replace("{{percentages.grand-total-failed}}", DECIMAL_FORMAT.format(percentages.getGrandTotalFailed()))
                .replace("{{percentages.grand-total-aborted}}", DECIMAL_FORMAT.format(percentages.getGrandTotalAborted()))
                .replace("{{percentages.grand-total-disabled}}", DECIMAL_FORMAT.format(percentages.getGrandTotalDisabled()))
                .replace("{{percentages.grand-total-not-run}}", DECIMAL_FORMAT.format(percentages.getGrandTotalNotRun()));
    }
}
