package com.paipeng.tifftagviewer.utils;

import com.paipeng.tifftagviewer.model.TiffTag;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;

public class TiffUtilsTest {
    @Test
    public void testReadTiffTag() throws IOException {
        String imagePath = "/Users/paipeng/watermark_scan_copy.tiff";
        Node node = TiffUtils.readTiffTag(imagePath);
        // com_sun_media_imageio_plugins_tiff_image_1.0
        NodeList childrenNodeList = node.getChildNodes();
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

                    }
                }
            }
        }
    }
}
