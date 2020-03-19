package com.paipeng.tifftagviewer.utils;

import com.paipeng.tifftagviewer.model.TiffTag;
import com.twelvemonkeys.imageio.util.ProgressListenerBase;
import com.twelvemonkeys.xml.XMLSerializer;
import javafx.collections.ObservableList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;

public class TiffUtils {
    public static Node readTiffTag(String imagePath) throws IOException {
        ImageIO.setUseCache(false);

        //String imagePath = "/Users/paipeng/testPDFA3.tif";
        File file = new File(imagePath);

        ImageInputStream input = ImageIO.createImageInputStream(file);
        if (input == null) {
            System.err.println("Could not read file: " + file);
            return null;
        }

        //deregisterOSXTIFFImageReaderSpi();

        Iterator<ImageReader> readers = ImageIO.getImageReaders(input);

        if (!readers.hasNext()) {
            System.err.println("No reader for: " + file);
            return null;
        }

        ImageReader reader = readers.next();
        System.err.println("Reading using: " + reader);

        reader.addIIOReadWarningListener(new IIOReadWarningListener() {
            public void warningOccurred(ImageReader source, String warning) {
                System.err.println("Warning: " + imagePath + ": " + warning);
            }
        });
        reader.addIIOReadProgressListener(new ProgressListenerBase() {
            private static final int MAX_W = 78;
            int lastProgress = 0;

            @Override
            public void imageStarted(ImageReader source, int imageIndex) {
                System.out.print("[");
            }

            @Override
            public void imageProgress(ImageReader source, float percentageDone) {
                int steps = ((int) (percentageDone * MAX_W) / 100);

                for (int i = lastProgress; i < steps; i++) {
                    System.out.print(".");
                }

                System.out.flush();
                lastProgress = steps;
            }

            @Override
            public void imageComplete(ImageReader source) {
                for (int i = lastProgress; i < MAX_W; i++) {
                    System.out.print(".");
                }

                System.out.println("]");
            }
        });

        reader.setInput(input);

        try {
            ImageReadParam param = reader.getDefaultReadParam();

            if (param.getClass().getName().equals("com.twelvemonkeys.imageio.plugins.svg.SVGReadParam")) {
                Method setBaseURI = param.getClass().getMethod("setBaseURI", String.class);
                String uri = file.getAbsoluteFile().toURI().toString();
                setBaseURI.invoke(param, uri);
            }

            int numImages = reader.getNumImages(true);
            for (int imageNo = 0; imageNo < numImages; imageNo++) {
                //            if (args.length > 1) {
                //                int sub = Integer.parseInt(args[1]);
                //                int sub = 4;
                //                param.setSourceSubsampling(sub, sub, 0, 0);
                //            }

                try {
                    long start = System.currentTimeMillis();
//                    int width = reader.getWidth(imageNo);
//                    int height = reader.getHeight(imageNo);
//                    param.setSourceRegion(new Rectangle(width / 4, height / 4, width / 2, height / 2));
//                    param.setSourceRegion(new Rectangle(100, 300, 400, 400));
//                    param.setSourceRegion(new Rectangle(95, 105, 100, 100));
//                    param.setSourceRegion(new Rectangle(3, 3, 9, 9));
//                    param.setDestinationOffset(new Point(50, 150));
//                    param.setSourceSubsampling(2, 2, 0, 0);
//                    param.setSourceSubsampling(3, 3, 0, 0);
                    BufferedImage image = reader.read(imageNo, param);
                    System.err.println("Read time: " + (System.currentTimeMillis() - start) + " ms");

                    IIOMetadata metadata = reader.getImageMetadata(imageNo);
                    if (metadata != null) {
                        if (metadata.getNativeMetadataFormatName() != null) {
                            Node tree = metadata.getAsTree(metadata.getNativeMetadataFormatName());
                            //replaceBytesWithUndefined((IIOMetadataNode) tree);
                            new XMLSerializer(System.out, "UTF-8").serialize(tree, false);
                            return tree;
                        }
                        /*else*/
                        if (metadata.isStandardMetadataFormatSupported()) {
                            new XMLSerializer(System.out, "UTF-8").serialize(metadata.getAsTree(IIOMetadataFormatImpl.standardMetadataFormatName), false);
                            return metadata.getAsTree(IIOMetadataFormatImpl.standardMetadataFormatName);
                        }
                    }

                    System.err.println("image: " + image);

//                        int w = image.getWidth();
//                        int h = image.getHeight();
//
//                        int newW = h;
//                        int newH = w;
//
//                        AffineTransform xform =  AffineTransform.getTranslateInstance((newW - w) / 2.0, (newH - h) / 2.0);
//                        xform.concatenate(AffineTransform.getQuadrantRotateInstance(3, w / 2.0, h / 2.0));
//                        AffineTransformOp op = new AffineTransformOp(xform, null);
//
//                        image = op.filter(image, null);
//
//                        System.err.println("image: " + image);
//
//                    File tempFile = File.createTempFile("lzw-", ".bin");
//                    byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
//                    FileOutputStream stream = new FileOutputStream(tempFile);
//                    try {
//                        FileUtil.copy(new ByteArrayInputStream(data, 45 * image.getWidth() * 3, 5 * image.getWidth() * 3), stream);
//
//                        showIt(image.getSubimage(0, 45, image.getWidth(), 5), tempFile.getAbsolutePath());
//                    }
//                    finally {
//                        stream.close();
//                    }
//
//                    System.err.println("tempFile: " + tempFile.getAbsolutePath());

                    //            image = new ResampleOp(reader.getWidth(0) / 4, reader.getHeight(0) / 4, ResampleOp.FILTER_LANCZOS).filter(image, null);
//
//                int maxW = 800;
//                int maxH = 800;
//
//                if (image.getWidth() > maxW || image.getHeight() > maxH) {
//                    start = System.currentTimeMillis();
//                    float aspect = reader.getAspectRatio(0);
//                    if (aspect >= 1f) {
//                        image = ImageUtil.createResampled(image, maxW, Math.round(maxW / aspect), Image.SCALE_DEFAULT);
//                    }
//                    else {
//                        image = ImageUtil.createResampled(image, Math.round(maxH * aspect), maxH, Image.SCALE_DEFAULT);
//                    }
//    //                    System.err.println("Scale time: " + (System.currentTimeMillis() - start) + " ms");
//                }

                    if (image != null && image.getType() == BufferedImage.TYPE_CUSTOM) {
                        start = System.currentTimeMillis();
                        image = new ColorConvertOp(null).filter(image, new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB));
                        System.err.println("Conversion time: " + (System.currentTimeMillis() - start) + " ms");
                    }

                    //showIt(image, String.format("Image: %s [%d x %d]", file.getName(), reader.getWidth(imageNo), reader.getHeight(imageNo)));

                    try {
                        int numThumbnails = reader.getNumThumbnails(imageNo);
                        for (int thumbnailNo = 0; thumbnailNo < numThumbnails; thumbnailNo++) {
                            BufferedImage thumbnail = reader.readThumbnail(imageNo, thumbnailNo);
                            //                        System.err.println("thumbnail: " + thumbnail);
                            //showIt(thumbnail, String.format("Thumbnail: %s [%d x %d]", file.getName(), thumbnail.getWidth(), thumbnail.getHeight()));
                        }
                    }
                    catch (IIOException e) {
                        System.err.println("Could not read thumbnails: " + e.getMessage());
                        e.printStackTrace();
                    }
                    return null;
                }
                catch (Throwable t) {
                    System.err.println(file + " image " + imageNo + " can't be read:");
                    t.printStackTrace();
                }
            }
        }
        catch (Throwable t) {
            System.err.println(file + " can't be read:");
            t.printStackTrace();
        }
        finally {
            input.close();
        }
        return null;
    }

    public static void updateTableView(ObservableList<Object> tagTableViewList, Node tiffTagNode) {
        //for(int i = 0; i < tiffTagNode.get.getLength(); i++){
        NodeList childrenNodeList = tiffTagNode.getChildNodes();
        if (childrenNodeList != null) {
            for (int i = 0; i < childrenNodeList.getLength(); i++) {

                // TIFFIFD
                Node tiffIFDNodeList = childrenNodeList.item(i);

                NodeList tiffFieldNodeList = tiffIFDNodeList.getChildNodes();
                for (int j = 0; j < tiffFieldNodeList.getLength(); j ++) {
                    Node nNode = tiffFieldNodeList.item(j);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element elem = (Element) nNode;
                        System.out.println(elem.getNodeName());
                        // tag name
                        System.out.println(elem.getAttribute("name"));


                        Element value = (Element) nNode.getChildNodes().item(0).getFirstChild();
                        System.out.println(value.getAttribute("value"));


                        TiffTag tiffTag = new TiffTag(elem.getAttribute("name"), value.getAttribute("value"));

                        tagTableViewList.add(tiffTag);
                    }
                }
            }
        }
    }
}
