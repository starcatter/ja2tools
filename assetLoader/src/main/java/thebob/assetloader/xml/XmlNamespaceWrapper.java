/* 
 * The MIT License
 *
 * Copyright 2017 the_bob.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package thebob.assetloader.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.util.StreamReaderDelegate;

/**
 *
 * @author the_bob
 * 
 * So there's this jaxb2 weirdness about requiring namespace to be specified for pretty much everything, even if schema form is set to unqualified.
 * This class is a way of fooling the marshaller into thinking we're actually crazy enough to prefix every damn xml element and attribute.
 * It should work as long as namespaces are close enough to what package names can be.
 * An alternative would be to set form=unqualified in the schema and then delete package-info.java files that are generated - this seems to work too.
 */
class XmlNamespaceWrapper extends StreamReaderDelegate {

    private String packageName;

    public XmlNamespaceWrapper(String xmlFile, Class baseClass) throws XMLStreamException, FileNotFoundException {
        super(XMLInputFactory.newFactory().createXMLStreamReader(new FileInputStream(xmlFile)));
        packageName = baseClass.getPackage().getName().replace('.', '/');
    }
    
    public XmlNamespaceWrapper(FileInputStream xmlFile, Class baseClass) throws XMLStreamException, FileNotFoundException {
        super(XMLInputFactory.newFactory().createXMLStreamReader(xmlFile));
        packageName = baseClass.getPackage().getName().replace('.', '/');
    }

    @Override
    public String getAttributeNamespace(int arg0) {
        return packageName;
    }

    @Override
    public String getNamespaceURI() {
        return packageName;
    }
    
}
