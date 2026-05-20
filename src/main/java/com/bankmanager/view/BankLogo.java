package com.bankmanager.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Custom component vẽ logo thương hiệu BankManager Pro cực kỳ sang trọng và độc đáo.
 * Sử dụng Graphics2D vector vẽ trực tiếp bằng thuật toán khử răng cưa và dải màu Gradient,
 * mang lại giao diện nhất quán, sắc nét ở mọi độ phân giải màn hình mà không cần dùng file ảnh tĩnh.
 */
public class BankLogo extends JComponent {
    private final int size;
    private final boolean isLightMode;
    private final Color goldStart = new Color(255, 215, 0); // Vàng Hoàng Gia sáng
    private final Color goldEnd = new Color(184, 134, 11);  // Vàng Đồng sang trọng
    private final Color cyanStart = new Color(0, 242, 254); // Xanh Neon hiện đại
    private final Color cyanEnd = new Color(79, 172, 254);  // Xanh Da Trời cao cấp

    public BankLogo(int size, boolean isLightMode) {
        this.size = size;
        this.isLightMode = isLightMode;
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Bật chế độ khử răng cưa cực đẹp
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();
        int r = Math.min(w, h) - 6;
        int x = (w - r) / 2;
        int y = (h - r) / 2;

        // 1. Vẽ vòng hào quang phát sáng bên ngoài (Outer Glow)
        if (isLightMode) {
            g2d.setColor(new Color(15, 34, 64, 25)); // Navy nhẹ
        } else {
            g2d.setColor(new Color(0, 242, 254, 35)); // Neon Cyan mờ cực chất cho dark/navy mode
        }
        g2d.setStroke(new BasicStroke(4f));
        g2d.drawOval(x + 2, y + 2, r - 4, r - 4);

        // 2. Vẽ vòng tròn nền chính với dải màu Gradient chuyển tiếp mượt mà
        GradientPaint bgGp;
        if (isLightMode) {
            // Nền trắng/xám nhẹ phối viền
            bgGp = new GradientPaint(
                x, y, new Color(245, 247, 250),
                x + r, y + r, new Color(225, 230, 240)
            );
        } else {
            // Nền xanh Navy sâu thẳm cực kỳ cao cấp
            bgGp = new GradientPaint(
                x, y, new Color(15, 34, 64),
                x + r, y + r, new Color(6, 15, 30)
            );
        }
        g2d.setPaint(bgGp);
        g2d.fillOval(x + 4, y + 4, r - 8, r - 8);

        // 3. Vẽ đường viền tròn kép sang trọng bằng dải màu Gradient (Gold hoặc Cyan tùy chế độ)
        GradientPaint borderGp;
        if (isLightMode) {
            borderGp = new GradientPaint(x + 4, y + 4, goldStart, x + r - 4, y + r - 4, goldEnd);
        } else {
            borderGp = new GradientPaint(x + 4, y + 4, cyanStart, x + r - 4, y + r - 4, cyanEnd);
        }
        g2d.setPaint(borderGp);
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawOval(x + 4, y + 4, r - 8, r - 8);

        // 4. Thiết kế biểu tượng kiến trúc Ngân hàng Trung tâm cách điệu ở tâm
        int cx = w / 2;
        int cy = h / 2;
        int symSize = r / 2; // Kích thước của biểu tượng nội bộ

        // Dải màu cho biểu tượng chính
        GradientPaint symbolGp;
        if (isLightMode) {
            symbolGp = new GradientPaint(cx - symSize/2, cy - symSize/2, new Color(15, 34, 64),
                                         cx + symSize/2, cy + symSize/2, new Color(40, 76, 128));
        } else {
            symbolGp = new GradientPaint(cx - symSize/2, cy - symSize/2, goldStart,
                                         cx + symSize/2, cy + symSize/2, goldEnd);
        }
        g2d.setPaint(symbolGp);

        // A. Mái nhà hình tam giác (Greek Temple Pediment) thể hiện sự uy tín và bền vững
        Path2D roof = new Path2D.Double();
        roof.moveTo(cx, cy - symSize * 0.45);
        roof.lineTo(cx - symSize * 0.5, cy - symSize * 0.15);
        roof.lineTo(cx + symSize * 0.5, cy - symSize * 0.15);
        roof.closePath();
        g2d.fill(roof);

        // B. Thanh xà ngang (Architrave)
        int archH = Math.max(2, r / 28);
        g2d.fillRect(cx - (int)(symSize * 0.45), cy - (int)(symSize * 0.15) + 1, (int)(symSize * 0.9), archH);

        // C. Ba cột trụ vuông cách điệu thể hiện: "An Toàn - Phát Triển - Thịnh Vượng"
        int pillarW = Math.max(2, symSize / 7);
        int pillarH = (int)(symSize * 0.35);
        int pillarY = cy - (int)(symSize * 0.15) + 1 + archH + 2;

        // Cột trái
        g2d.fillRect(cx - (int)(symSize * 0.3) - pillarW / 2, pillarY, pillarW, pillarH);
        // Cột giữa
        g2d.fillRect(cx - pillarW / 2, pillarY, pillarW, pillarH);
        // Cột phải
        g2d.fillRect(cx + (int)(symSize * 0.3) - pillarW / 2, pillarY, pillarW, pillarH);

        // D. Bậc thềm đa tầng thể hiện sự nâng đỡ vững chắc
        int baseY = pillarY + pillarH;
        int stepH1 = Math.max(2, r / 32);
        int stepH2 = Math.max(3, r / 26);
        g2d.fillRect(cx - (int)(symSize * 0.45), baseY, (int)(symSize * 0.9), stepH1);
        g2d.fillRect(cx - (int)(symSize * 0.52), baseY + stepH1 + 2, (int)(symSize * 1.04), stepH2);

        g2d.dispose();
    }
}
