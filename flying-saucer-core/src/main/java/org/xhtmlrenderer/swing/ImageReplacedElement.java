/*
 * {{{ header & license
 * Copyright (c) 2007 Wisconsin Court System
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * }}}
 */
package org.xhtmlrenderer.swing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.util.Configuration;
import org.xhtmlrenderer.util.ImageUtil;

/**
 * An ImageReplacedElement is a {@link ReplacedElement} that contains a {@link java.awt.Image}. It's used as a
 * container for images included within XML being rendered. The image contained is immutable.
 */
public class ImageReplacedElement implements ReplacedElement {
    protected Image _image;
    
    private Point _location = new Point(0, 0);

    protected ImageReplacedElement() {
    }

    /**
     * Creates a new ImageReplacedElement and scales it to the size specified if either width or height has a valid
     * value (values are > -1), otherwise original size is preserved. The idea is that the image was loaded at
     * a certain size (that's the Image instance here) and that at the time we create the ImageReplacedElement
     * we have a target W/H we want to use.
     *
     * @param image An image.
     * @param targetWidth The width we'd like the image to have, in pixels.
     * @param targetHeight The height we'd like the image to have, in pixels.
     */
    
    public ImageReplacedElement(Image image, int targetWidth, int targetHeight) {
		if (targetWidth > 0 || targetHeight > 0) {
            int w = image.getWidth(null);
            int h = image.getHeight(null);

		    int newW = targetWidth;
		    int newH = targetHeight;

		    if (newW == -1) {
		        newW = (int)(w * ((double)newH / h));
		    }

	        if (newH == -1) {
	            newH = (int)(h * ((double)newW / w));
	        }
	        
			if (w != newW || h != newH) {
				boolean hasAlpha = false;
				if(image instanceof BufferedImage) {
					ColorModel cm = ((BufferedImage) image).getColorModel();
					hasAlpha = cm.hasAlpha();
				}
				
                if (image instanceof BufferedImage) {
                	if(hasAlpha) {
                        BufferedImage src = (BufferedImage) image;
                        BufferedImage scaled = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = scaled.createGraphics();
                        g2d.setComposite(AlphaComposite.Src);
                        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g2d.drawImage(src, 0, 0, newW, newH, null);
                        g2d.dispose();
                        image = scaled;
                	}else {
                		image = ImageUtil.getScaledInstance((BufferedImage) image, newW, newH);
                	}
                    
                } else {
                   String scalingType = Configuration.valueFor("xr.image.scale", "HIGH").trim() ;

                   if(scalingType.equalsIgnoreCase("HIGH") || scalingType.equalsIgnoreCase("MID") ){
                       image = image.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
                   } else{
                    image = image.getScaledInstance(newW, newH, Image.SCALE_FAST);
                }
            }
        }
        }
        _image = image;
    }

    /** {@inheritDoc} */
    public void detach(LayoutContext c) {
        // nothing to do in this case
    }

    /** {@inheritDoc} */
    public int getIntrinsicHeight() {
        return _image.getHeight(null);
    }

    /** {@inheritDoc} */
    public int getIntrinsicWidth() {
        return _image.getWidth(null);
    }

    /** {@inheritDoc} */
    public Point getLocation() {
        return _location;
    }

    /** {@inheritDoc} */
    public boolean isRequiresInteractivePaint() {
        return true;
    }

    /** {@inheritDoc} */
    public void setLocation(int x, int y) {
        _location = new Point(x, y);
    }

    /**
     * The image we're replacing.
     * @return see desc
     */
    public Image getImage() {
        return _image;
    }

	public int getBaseline() {
		return 0;
	}

	public boolean hasBaseline() {
		return false;
	}
}
