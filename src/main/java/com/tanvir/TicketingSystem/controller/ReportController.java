package com.tanvir.TicketingSystem.controller;

import com.tanvir.TicketingSystem.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/daily")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> getDailyReport(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        ByteArrayInputStream bis = reportService.generateDailyReport(date);

        return createPdfResponse("daily_report_" + date + ".pdf", bis);
    }

    @GetMapping("/range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> getRangeReport(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        ByteArrayInputStream bis = reportService.generateRangeReport(startDate, endDate);

        return createPdfResponse("range_report_" + startDate + "_to_" + endDate + ".pdf", bis);
    }

    @GetMapping("/monthly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> getMonthlyReport(
            @RequestParam("year") int year,
            @RequestParam("month") int month) {

        ByteArrayInputStream bis = reportService.generateMonthlyReport(year, month);

        return createPdfResponse("monthly_report_" + year + "_" + month + ".pdf", bis);
    }

    @GetMapping("/yearly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> getYearlyReport(@RequestParam("year") int year) {

        ByteArrayInputStream bis = reportService.generateYearlyReport(year);

        return createPdfResponse("yearly_report_" + year + ".pdf", bis);
    }

    private ResponseEntity<InputStreamResource> createPdfResponse(String filename, ByteArrayInputStream bis) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=" + filename);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
