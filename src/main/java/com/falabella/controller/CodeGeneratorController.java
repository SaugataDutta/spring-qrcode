package com.falabella.controller;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/code")
public class CodeGeneratorController {

    @GetMapping(value = "encode", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] createQRCode(@RequestParam String text) throws WriterException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitMatrix matrix = new MultiFormatWriter().encode(text,
                BarcodeFormat.QR_CODE,
                200,
                200);
        MatrixToImageWriter.writeToStream(matrix,
                MediaType.IMAGE_PNG.getSubtype(),
                baos,
                new MatrixToImageConfig());
        return baos.toByteArray();
    }


    @PostMapping(value = "decode", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public String readQRCode(@RequestParam(required = false, value = "file") MultipartFile
            image) throws WriterException, IOException {

        File imageFile = new File(image.getOriginalFilename());
        FileUtils.writeByteArrayToFile(imageFile,
                image.getBytes());

        BufferedImage bufferedImage = ImageIO.read(imageFile);
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            System.out.println("There is no QR code in the image");
            return null;
        }
    }
}
