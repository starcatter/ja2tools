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
package thebob.ja2maptool.components;

import java.util.Optional;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import org.controlsfx.control.PropertySheet;

/**
 *
 * @author the_bob
 */
public class SimplePropertyItem implements PropertySheet.Item{
    
    String category;
    String name;
    String description;
    String value;

    public SimplePropertyItem(String name, String value, String category, String description) {
	this.category = category;
	this.name = name;
	this.description = description;
	this.value = value;
    }        
    
    @Override
    public Class<?> getType() {
	return "".getClass();
    }

    @Override
    public String getCategory() {
	return category;
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public String getDescription() {
	return description;
    }

    @Override
    public Object getValue() {
	return value;
    }

    @Override
    public void setValue(Object value) {
	value = (String)value.toString();
    }

    @Override
    public Optional<ObservableValue<? extends Object>> getObservableValue() {
	return Optional.empty();
    }
    
}
