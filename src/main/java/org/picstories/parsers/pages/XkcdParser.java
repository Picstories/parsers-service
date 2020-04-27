package org.picstories.parsers.pages;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.picstories.library.model.entity.comics.Comics;
import org.picstories.library.model.entity.page.Page;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author arman.shamenov
 */
@Component(XkcdParser.PARSER_CODE)
public class XkcdParser implements PageParser {

    public static final String PARSER_CODE = "xkcd-parser";
    private static final String BASE_URL = "https://xkcd.com/";

    @Override
    public String getParserCode() {
        return PARSER_CODE;
    }

    @Override
    public Page parsePage(Comics comic, Page prevPage) throws IOException {
        if (prevPage == null) {
            return parsePage(comic);
        }

        String connectionUrl = prevPage.getNextUrl();
        int number = prevPage.getNumber() + 1;

        return parsePage(comic, connectionUrl, number);
    }

    @Override
    public Page parsePage(Comics comic) throws IOException {
        return parsePage(comic, comic.getInitUrl(), 1);
    }

    @Override
    public Page parsePage(Comics comic, String url, int number) throws IOException {
        return parseContentByUrl(url)
                .comicId(comic.getId())
                .number(number)
                .id(comic.getId() + "_" + number)
                .nextUrl(BASE_URL + (number + 1))
                .build();
    }

    private Page.PageBuilder parseContentByUrl(String connectionUrl) throws IOException {
        Document document = Jsoup.connect(connectionUrl).get();
        return parseContent(document)
                .pageUrl(connectionUrl);
    }

    public Page.PageBuilder parseContent(Document document) {

        String title = document.select("div#ctitle").text();
        String description = document.select("div#comic > img").attr("title");

        Elements img = document.select("div#comic > img");
        String imageUrl = "https:" + img.attr("src");
        String largeImageUrl = img.attr("srcset");
        if (!largeImageUrl.isEmpty()) {
            largeImageUrl = "https:" + largeImageUrl;
        }

        return Page.builder()
                .description(description)
                .title(title)
                .images(List.of(new Page.Image(imageUrl, null, largeImageUrl)))
                .pageUrl(document.baseUri());
    }
}

