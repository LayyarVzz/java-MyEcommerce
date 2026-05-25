package com.example.myecommerce;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SalesReportAnalyticsTemplateTest {

    @Test
    void reportTemplatesExposeAnalyticsAndDailyWeeklyMonthlyCharts() throws IOException {
        for (String template : List.of(
                "src/main/resources/templates/admin/sales-report.html",
                "src/main/resources/templates/sales/sales-report.html"
        )) {
            String html = Files.readString(Path.of(template));

            assertThat(html).contains("reportData.trendAssessment");
            assertThat(html).contains("reportData.forecastDescription");
            assertThat(html).contains("reportData.anomalyLevel");
            assertThat(html).contains("dailyTrendChart");
            assertThat(html).contains("weeklyTrendChart");
            assertThat(html).contains("monthlyTrendChart");
            assertThat(html).contains("weeklyTrendLabels");
            assertThat(html).contains("monthlyTrendLabels");
        }
    }
}
