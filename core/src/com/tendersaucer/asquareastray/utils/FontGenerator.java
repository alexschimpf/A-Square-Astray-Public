package com.tendersaucer.asquareastray.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * https://github.com/jrenner/gdx-smart-font/blob/master/src/org/jrenner/smartfont/SmartFontGenerator.java
 */
public class FontGenerator {

    private static final String TAG = "SmartFontGenerator";
    private boolean forceGeneration;
    private String generatedFontDir;
    private int referenceScreenWidth;
    // TODO figure out optimal page size automatically
    private int pageSize;

    public FontGenerator() {
        forceGeneration = false;
        generatedFontDir = "generated-fonts/";
        referenceScreenWidth = 1280;
        pageSize = 512; // size of atlas pages for font pngs
    }

    public BitmapFont createFont(String fontName, float fontSize) {
        return createFont(fontName, (int)fontSize);
    }


    /** Will load font from file. If that fails, font will be generated and saved to file.
     * @param fontName the name of the font, i.e. "arial-small", "arial-large", "monospace-10"
     *                 This will be used for creating the font file names
     * @param fontSize size of font when screen width equals referenceScreenWidth */
    public BitmapFont createFont(String fontName, int fontSize) {
        BitmapFont font = null;
        // if fonts are already generated, just load from file
        Preferences fontPrefs = Gdx.app.getPreferences("org.jrenner.smartfont");
        int displayWidth = fontPrefs.getInteger("display-width", 0);
        int displayHeight = fontPrefs.getInteger("display-height", 0);
        boolean loaded = false;
        if (displayWidth == Gdx.graphics.getWidth() && displayHeight == Gdx.graphics.getHeight()) {
            try {
                // try to load from file
                font = new BitmapFont(getFontFile(fontName + ".fnt", fontSize));
                loaded = true;
            } catch (GdxRuntimeException e) {
                Gdx.app.error(TAG, e.getMessage());
            }
        }
        if (!loaded || forceGeneration) {
            forceGeneration = false;
            float width = Gdx.graphics.getWidth();
            float ratio = width / referenceScreenWidth; // use 1920x1280 as baseline, arbitrary
            float baseSize = 28f; // for 28 sized fonts at baseline width above

            // store screen width for detecting screen size change
            // on later startups, which will require font regeneration
            fontPrefs.putInteger("display-width", Gdx.graphics.getWidth());
            fontPrefs.putInteger("display-height", Gdx.graphics.getHeight());
            fontPrefs.flush();

            FileHandle fontFile = Gdx.files.internal("font.ttf");
            font = generateFontWriteFiles(fontName, fontFile, fontSize, pageSize, pageSize);
        }
        return font;
    }

    /** Convenience method for generating a font, and then writing the fnt and png files.
     * Writing a generated font to files allows the possibility of only generating the fonts when they are missing, otherwise
     * loading from a previously generated file.
     * @param fontFile
     * @param fontSize
     */
    private BitmapFont generateFontWriteFiles(String fontName, FileHandle fontFile, int fontSize, int pageWidth, int pageHeight) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);

        PixmapPacker packer = new PixmapPacker(pageWidth, pageHeight, Pixmap.Format.RGBA8888, 2, false);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSize;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
        parameter.flip = false;
        parameter.packer = packer;
        FreeTypeFontGenerator.FreeTypeBitmapFontData fontData = generator.generateData(parameter);
        Array<PixmapPacker.Page> pages = packer.getPages();
        Array<TextureRegion> texRegions = new Array<>();
        for (int i = 0; i < pages.size; i++) {
            PixmapPacker.Page p = pages.get(i);
            Texture tex = new Texture(
                    new PixmapTextureData(p.getPixmap(), p.getPixmap().getFormat(), false, false, true)) {
                @Override
                public void dispose() {
                    super.dispose();
                    getTextureData().consumePixmap().dispose();
                }
            };
            tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            texRegions.add(new TextureRegion(tex));
        }
        BitmapFont font = new BitmapFont((BitmapFont.BitmapFontData) fontData, texRegions, false);
        saveFontToFile(font, fontSize, fontName, packer);
        generator.dispose();
        packer.dispose();
        return font;
    }

    private void saveFontToFile(BitmapFont font, int fontSize, String fontName, PixmapPacker packer) {
        FileHandle fontFile = getFontFile(fontName + ".fnt", fontSize); // .fnt path
        FileHandle pixmapDir = getFontFile(fontName, fontSize); // png dir path
        BitmapFontWriter.setOutputFormat(BitmapFontWriter.OutputFormat.Text);

        String[] pageRefs = BitmapFontWriter.writePixmaps(packer.getPages(), pixmapDir, fontName);
        // here we must add the png dir to the page refs
        for (int i = 0; i < pageRefs.length; i++) {
            pageRefs[i] = fontSize + "_" + fontName + "/" + pageRefs[i];
        }
        BitmapFontWriter.writeFont(font.getData(), pageRefs, fontFile, new BitmapFontWriter.FontInfo(fontName, fontSize), 1, 1);
    }

    private FileHandle getFontFile(String filename, int fontSize) {
        return Gdx.files.local(generatedFontDir + fontSize + "_" + filename);
    }

    public void setForceGeneration(boolean force) {
        forceGeneration = force;
    }

    public boolean getForceGeneration() {
        return forceGeneration;
    }

    /** Set directory for storing generated fonts */
    public void setGeneratedFontDir(String dir) {
        generatedFontDir = dir;
    }

    public String getGeneratedFontDir() {
        return generatedFontDir;
    }

    /** Set the reference screen width for computing sizes.  If reference width is 1280, and screen width is 1280
     * Then the fontSize paramater will be unaltered when creating a font.  If the screen width is 720, the font size
     * will by scaled down to (720 / 1280) of original size. */
    public void setReferenceScreenWidth(int width) {
        referenceScreenWidth = width;
    }

    public int getReferenceScreenWidth() {
        return referenceScreenWidth;
    }

    /** Set the width and height of the png files to which the fonts will be saved.
     * In the future it would be nice for page size to be automatically set to the optimal size
     * by the font generator.  In the mean time it must be set manually. */
    public void setPageSize(int size) {
        pageSize = size;
    }

    public int getPageSize() {
        return pageSize;
    }
}
