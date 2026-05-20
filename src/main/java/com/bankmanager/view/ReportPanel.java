package com.bankmanager.view;

import com.bankmanager.dao.AccountDAO;
import com.bankmanager.dao.CustomerDAO;
import com.bankmanager.dao.TransactionDAO;
import com.bankmanager.model.Account;
import com.bankmanager.model.Customer;
import com.bankmanager.model.Transaction;
import com.bankmanager.util.DBConnection;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ReportPanel extends JPanel {
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    private JPanel chartContainer;
    private JLabel lblTotalCust, lblTotalBalance, lblTotalTrans;

    public ReportPanel() {
        initComponents();
        loadStats();
        drawChart();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Tiêu đề
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        JLabel lblTitle = new JLabel("📊 Thống Kê & Báo Cáo Tài Chính");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 34, 64));
        titlePanel.add(lblTitle, BorderLayout.WEST);

        // Nút xuất báo cáo nhanh bên phải
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setBackground(Color.WHITE);
        
        JButton btnExportExcel = new JButton("📥 Xuất Báo Cáo Excel");
        btnExportExcel.setBackground(new Color(40, 167, 69)); // màu xanh lá
        btnExportExcel.setForeground(Color.WHITE);
        btnExportExcel.addActionListener(e -> exportToExcel());

        JButton btnExportPdf = new JButton("📄 Xuất Báo Cáo PDF");
        btnExportPdf.setBackground(new Color(220, 53, 69)); // màu đỏ
        btnExportPdf.setForeground(Color.WHITE);
        btnExportPdf.addActionListener(e -> exportToPdf());

        actionPanel.add(btnExportExcel);
        actionPanel.add(btnExportPdf);
        titlePanel.add(actionPanel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);

        // 2. Panel các thẻ chỉ số (Dashboard Cards) ở giữa
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        cardsPanel.setBackground(Color.WHITE);
        cardsPanel.setPreferredSize(new Dimension(0, 100));

        lblTotalCust = createStatCard(cardsPanel, "Tổng số Khách hàng", "0", new Color(0, 123, 255));
        lblTotalBalance = createStatCard(cardsPanel, "Tổng tiền gửi hệ thống", "0 VND", new Color(40, 167, 69));
        lblTotalTrans = createStatCard(cardsPanel, "Tổng số lượng Giao dịch", "0", new Color(255, 193, 7));

        // 3. Khu vực vẽ biểu đồ (Biểu đồ tròn cơ cấu loại giao dịch)
        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Bố cục chính
        JPanel mainGrid = new JPanel(new BorderLayout(15, 15));
        mainGrid.setBackground(Color.WHITE);
        mainGrid.add(cardsPanel, BorderLayout.NORTH);
        mainGrid.add(chartContainer, BorderLayout.CENTER);

        add(mainGrid, BorderLayout.CENTER);
    }

    private JLabel createStatCard(JPanel parent, String title, String value, Color themeColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, themeColor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 230, 230)),
                        new EmptyBorder(12, 15, 12, 15)
                )
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValue.setForeground(new Color(15, 34, 64));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        parent.add(card);
        return lblValue;
    }

    private void loadStats() {
        // Tải các chỉ số thực tế từ DB
        List<Customer> customers = customerDAO.getAll();
        lblTotalCust.setText(String.valueOf(customers.size()));

        List<Transaction> transactions = transactionDAO.getAll();
        lblTotalTrans.setText(String.valueOf(transactions.size()));

        BigDecimal totalBal = BigDecimal.ZERO;
        List<Account> accounts = accountDAO.getAll();
        for (Account a : accounts) {
            totalBal = totalBal.add(a.getBalance());
        }
        lblTotalBalance.setText(String.format("%,.2f VND", totalBal));
    }

    private void drawChart() {
        chartContainer.removeAll();

        DefaultPieDataset dataset = new DefaultPieDataset();
        
        // Thống kê số lượng các loại giao dịch trong CSDL
        int deposits = 0, withdraws = 0, transfers = 0, bills = 0;
        List<Transaction> list = transactionDAO.getAll();
        for (Transaction t : list) {
            if ("DEPOSIT".equalsIgnoreCase(t.getTransactionType())) deposits++;
            else if ("WITHDRAW".equalsIgnoreCase(t.getTransactionType())) withdraws++;
            else if ("TRANSFER".equalsIgnoreCase(t.getTransactionType())) transfers++;
            else if ("BILL_PAYMENT".equalsIgnoreCase(t.getTransactionType())) bills++;
        }

        dataset.setValue("Nạp tiền (" + deposits + ")", deposits);
        dataset.setValue("Rút tiền (" + withdraws + ")", withdraws);
        dataset.setValue("Chuyển khoản (" + transfers + ")", transfers);
        dataset.setValue("Thanh toán hóa đơn (" + bills + ")", bills);

        JFreeChart chart = ChartFactory.createPieChart(
                "Cơ Cấu Các Loại Giao Dịch Trong Hệ Thống",
                dataset,
                true, // hiển thị chú thích
                true,
                false
        );

        // Làm đẹp biểu đồ
        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        plot.setSectionPaint("Nạp tiền (" + deposits + ")", new Color(40, 167, 69));     // màu xanh lá
        plot.setSectionPaint("Rút tiền (" + withdraws + ")", new Color(255, 193, 7));     // màu vàng
        plot.setSectionPaint("Chuyển khoản (" + transfers + ")", new Color(0, 123, 255));  // màu xanh dương
        plot.setSectionPaint("Thanh toán hóa đơn (" + bills + ")", new Color(111, 66, 193));// màu tím

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.WHITE);
        chartContainer.add(chartPanel, BorderLayout.CENTER);
        chartContainer.validate();
    }

    /**
     * Xuất danh sách giao dịch ra file Excel (sử dụng Apache POI)
     */
    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu Báo Cáo Excel");
        fileChooser.setSelectedFile(new File("BaoCaoGiaoDich.xlsx"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Lịch sử giao dịch");
                
                // Tạo hàng Header
                Row headerRow = sheet.createRow(0);
                String[] headers = {"Mã GD", "Tài Khoản Gửi", "Tài Khoản Nhận", "Loại Giao Dịch", "Số Tiền (VND)", "Phí", "Nội Dung", "Thời Gian"};
                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                }
                
                // Ghi dữ liệu giao dịch
                List<Transaction> list = transactionDAO.getAll();
                int rowIdx = 1;
                for (Transaction t : list) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(t.getTransactionId());
                    row.createCell(1).setCellValue(t.getFromAccount() != null ? t.getFromAccount() : "Nạp tiền mặt");
                    row.createCell(2).setCellValue(t.getToAccount() != null ? t.getToAccount() : "Rút tiền mặt");
                    row.createCell(3).setCellValue(t.getTransactionType());
                    row.createCell(4).setCellValue(t.getAmount().doubleValue());
                    row.createCell(5).setCellValue(t.getFee().doubleValue());
                    row.createCell(6).setCellValue(t.getDescription());
                    row.createCell(7).setCellValue(t.getTransactionDate().toString());
                }
                
                // Lưu ra file
                try (FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
                    workbook.write(fileOut);
                }
                
                JOptionPane.showMessageDialog(this, "Xuất báo cáo Excel thành công tại:\n" + fileToSave.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file Excel: " + ex.getMessage(), "Thất bại", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Xuất danh sách khách hàng ra file PDF (sử dụng iText 7)
     */
    private void exportToPdf() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu Báo Cáo PDF");
        fileChooser.setSelectedFile(new File("BaoCaoKhachHang.pdf"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            try {
                // Khởi tạo iText Writer và Document
                PdfWriter writer = new PdfWriter(new FileOutputStream(fileToSave));
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);
                
                // Tiêu đề báo cáo (Chữ Latin không dấu do font mặc định của iText)
                document.add(new Paragraph("NGAN HANG THUONG MAI - BANKMANAGER PRO").setBold().setFontSize(18));
                document.add(new Paragraph("DANH SACH KHACH HANG DANG KY TAI KHOAN").setFontSize(14).setItalic());
                document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));
                
                // Tạo bảng gồm 5 cột
                Table table = new Table(5);
                
                table.addCell("ID");
                table.addCell("CCCD");
                table.addCell("Ho va Ten");
                table.addCell("Dien Thoai");
                table.addCell("Ngay Sinh");
                
                List<Customer> customers = customerDAO.getAll();
                for (Customer c : customers) {
                    table.addCell(String.valueOf(c.getId()));
                    table.addCell(c.getCccd());
                    // Loại bỏ dấu tiếng Việt để tránh bị lỗi font trên iText7 mặc định (mô phỏng tiếng Việt không dấu)
                    table.addCell(removeAccent(c.getFullName()));
                    table.addCell(c.getPhone());
                    table.addCell(c.getDob().toString());
                }
                
                document.add(table);
                document.close();
                
                JOptionPane.showMessageDialog(this, "Xuất báo cáo PDF thành công tại:\n" + fileToSave.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file PDF: " + ex.getMessage(), "Thất bại", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Tiện ích loại bỏ tiếng Việt có dấu sang không dấu để tránh lỗi font trong PDF.
     */
    private String removeAccent(String s) {
        if (s == null) return "";
        String temp = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d");
    }
}
