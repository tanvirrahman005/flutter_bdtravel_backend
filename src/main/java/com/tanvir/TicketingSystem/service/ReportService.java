package com.tanvir.TicketingSystem.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.tanvir.TicketingSystem.entity.Booking;
import com.tanvir.TicketingSystem.entity.BookingSeat;
import com.tanvir.TicketingSystem.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ReportService {

    @Autowired
    private BookingRepository bookingRepository;

    public ByteArrayInputStream generateDailyReport(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        List<Booking> bookings = bookingRepository.findBookingsByDateRange(startOfDay, endOfDay);
        return createPdfReport("Daily Report - " + date.format(DateTimeFormatter.ISO_DATE), bookings);
    }

    public ByteArrayInputStream generateRangeReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        List<Booking> bookings = bookingRepository.findBookingsByDateRange(start, end);
        return createPdfReport("Range Report (" + startDate + " to " + endDate + ")", bookings);
    }

    public ByteArrayInputStream generateMonthlyReport(int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
        return generateRangeReport(startOfMonth, endOfMonth);
    }

    public ByteArrayInputStream generateYearlyReport(int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);
        return generateRangeReport(startOfYear, endOfYear);
    }

    public ByteArrayInputStream generateTicketPdf(Booking booking) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);

            // Header
            Paragraph header = new Paragraph("BD TICKET - E-TICKET", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph subHeader = new Paragraph("Booking Reference: " + booking.getBookingReference(), subHeaderFont);
            subHeader.setAlignment(Element.ALIGN_CENTER);
            document.add(subHeader);
            document.add(Chunk.NEWLINE);

            // Table structure
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            // Passenger Info
            PdfPCell cell = new PdfPCell(new Phrase("Passenger Information", subHeaderFont));
            cell.setColspan(2);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);

            table.addCell(new Phrase("Name:", boldFont));
            table.addCell(new Phrase(booking.getPassengerName(), normalFont));

            table.addCell(new Phrase("Phone:", boldFont));
            table.addCell(new Phrase(booking.getPassengerPhone(), normalFont));

            if (booking.getPassengerEmail() != null) {
                table.addCell(new Phrase("Email:", boldFont));
                table.addCell(new Phrase(booking.getPassengerEmail(), normalFont));
            }

            // Trip Details
            cell = new PdfPCell(new Phrase("Trip Details", subHeaderFont));
            cell.setColspan(2);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);

            table.addCell(new Phrase("Route:", boldFont));
            String route = booking.getSchedule().getRoute().getStartCity().getName() + " -> " +
                    booking.getSchedule().getRoute().getEndCity().getName();
            table.addCell(new Phrase(route, normalFont));

            table.addCell(new Phrase("Date:", boldFont));
            String date = booking.getSchedule().getDepartureTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            table.addCell(new Phrase(date, normalFont));

            table.addCell(new Phrase("Operator:", boldFont));
            table.addCell(new Phrase(booking.getSchedule().getVehicle().getTransportCompany().getName(), normalFont));

            table.addCell(new Phrase("Vehicle:", boldFont));
            table.addCell(new Phrase(booking.getSchedule().getVehicle().getModel() + " ("
                    + booking.getSchedule().getVehicle().getVehicleNumber() + ")", normalFont));

            // Seats
            cell = new PdfPCell(new Phrase("Seat Information", subHeaderFont));
            cell.setColspan(2);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);

            StringBuilder seatsStr = new StringBuilder();
            for (BookingSeat seat : booking.getSeats()) {
                seatsStr.append(seat.getSeatLayout().getSeatNumber()).append(", ");
            }
            // Remove trailing comma
            if (seatsStr.length() > 2) {
                seatsStr.setLength(seatsStr.length() - 2);
            }

            table.addCell(new Phrase("Seats:", boldFont));
            table.addCell(new Phrase(seatsStr.toString(), normalFont));

            table.addCell(new Phrase("Total Amount:", boldFont));
            table.addCell(new Phrase("BDT " + booking.getTotalAmount(), normalFont));

            document.add(table);

            // Footer
            document.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph("Please arrive 15 minutes before departure. Happy Journey!", normalFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private ByteArrayInputStream createPdfReport(String title, List<Booking> bookings) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Font Styles
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

            // Title
            Paragraph titlePara = new Paragraph(title, titleFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            document.add(titlePara);
            document.add(Chunk.NEWLINE);

            // Summary Stats
            double totalRevenue = bookings.stream()
                    .mapToDouble(b -> b.getTotalAmount().doubleValue())
                    .sum();

            Paragraph stats = new Paragraph("Total Bookings: " + bookings.size() +
                    " | Total Revenue: ৳" + String.format("%.2f", totalRevenue));
            stats.setAlignment(Element.ALIGN_CENTER);
            document.add(stats);
            document.add(Chunk.NEWLINE);

            // Table
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 1, 3, 3, 2, 2, 2 });

            // Table Header
            Stream.of("ID", "Booking Ref", "Customer", "Date", "Status", "Amount")
                    .forEach(headerTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.DARK_GRAY);
                        header.setBorderWidth(1);
                        header.setPhrase(new Phrase(headerTitle, headerFont));
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        header.setPadding(8);
                        table.addCell(header);
                    });

            // Table Data
            for (Booking booking : bookings) {
                addCell(table, String.valueOf(booking.getId()), dataFont);
                addCell(table, booking.getBookingReference(), dataFont);
                // Handle potential null user or name
                String customerName = booking.getUser() != null ? booking.getUser().getUserName() : "Guest";
                addCell(table, customerName, dataFont);
                addCell(table,
                        booking.getBookingDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        dataFont);
                addCell(table, booking.getBookingStatus().toString(), dataFont);
                addCell(table, "৳" + booking.getTotalAmount(), dataFont);
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }
}
