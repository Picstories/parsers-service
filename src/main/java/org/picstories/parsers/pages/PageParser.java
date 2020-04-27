package org.picstories.parsers.pages;

import org.picstories.library.model.entity.comics.Comics;
import org.picstories.library.model.entity.page.Page;

import java.io.IOException;

/**
 * @author arman.shamenov
 */
public interface PageParser {
    String getParserCode();

    Page parsePage(Comics comic) throws IOException;

    Page parsePage(Comics comic, Page prevPage) throws IOException;

    Page parsePage(Comics comic, String url, int number) throws IOException;
}
