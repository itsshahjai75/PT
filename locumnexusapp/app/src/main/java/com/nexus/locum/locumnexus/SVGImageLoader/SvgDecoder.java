package com.nexus.locum.locumnexus.SVGImageLoader;

/**
 * Created by Jay-Andriod on 03-Oct-17.
 */

import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.caverock.androidsvg.PreserveAspectRatio;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Decodes an SVG internal representation from an {@link InputStream}.
 */
public class SvgDecoder implements ResourceDecoder<InputStream, SVG> {

    float w,h;
    public SvgDecoder(float width,float height) {
        w=width;
        h=height;
    }

    public Resource<SVG> decode(InputStream source, int width, int height) throws IOException {
        try {
            SVG svg = SVG.getFromInputStream(source);
            svg.setDocumentWidth(w);
            svg.setDocumentHeight(h);
            svg.setDocumentPreserveAspectRatio(PreserveAspectRatio.STRETCH);
            return new SimpleResource<SVG>(svg);
        } catch (SVGParseException ex) {
            throw new IOException("Cannot load SVG from stream", ex);
        }
    }

    @Override
    public String getId() {
        return "SvgDecoder.com.bumptech.svgsample.app";
    }
}