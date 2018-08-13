package com.falabella.controller;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;

@RestController
@RequestMapping("/api/v1/code")
public class CodeGeneratorController {

    private static BufferedImage getQRImage(String qrCodeText, int size)
            throws WriterException, IOException {
        // Create the ByteMatrix for the QR-Code that encodes the given String
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION,
                ErrorCorrectionLevel.L);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText,
                BarcodeFormat.QR_CODE,
                size,
                size,
                hintMap);
        // Make the BufferedImage that are to hold the QRCode
        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth,
                matrixWidth,
                BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0,
                0,
                matrixWidth,
                matrixWidth);
        // Paint and save the image using the ByteMatrix
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i,
                        j)) {
                    graphics.fillRect(i,
                            j,
                            1,
                            1);
                }
            }
        }
        return image;
    }

    @GetMapping(produces = MediaType.IMAGE_GIF_VALUE)
    public byte[] getQRCode() throws WriterException {

        String qrCodeText = "Name:Saugata Dutta, Age:32, Country:India";
        int size = 250;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageOutputStream ios = new MemoryCacheImageOutputStream(out);

        try {
            if (!ImageIO.write(getQRImage(qrCodeText,
                    size),
                    "PNG",
                    ios)) {
                throw new IOException("ImageIO.write failed");
            }
            ios.close();
        } catch (IOException ex) {
            throw new RuntimeException("saveImage: " + ex.getMessage());
        }
        return out.toByteArray();
    }
}
