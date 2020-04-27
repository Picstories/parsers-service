package org.picstories.parsers.service;

import org.picstories.parsers.pages.PageParser;
import org.springframework.stereotype.Service;

import org.picstories.library.model.entity.comics.Comics;
import org.picstories.library.model.entity.page.Page;
import org.picstories.library.model.kafka.parsers.ParseTask;
import org.picstories.library.model.kafka.parsers.UpdateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.kafka.receiver.ReceiverRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author arman.shamenov
 */
@Service
public class ParsersService {
    private static final Logger logger = LoggerFactory.getLogger(ParsersService.class);

    @Value("${parser.max-per-task}")
    private int maxPerTask;

    private final Map<String, PageParser> pageParsers;
    private final UpdateComicsSender updateComicsSender;

    public ParsersService(Map<String, PageParser> pageParsers,
                          UpdateComicsSender updateComicsSender) {
        this.pageParsers = pageParsers;
        this.updateComicsSender = updateComicsSender;
    }

    public UpdateTask consume(ReceiverRecord<String, ParseTask> receiverRecord) {
        return consume(receiverRecord.value(), true);
    }

    /**
     * @param taskMessage     comic to parse and its last parsed page
     * @param send           if we want to send the pages to kafka
     * @return list of parsed comics
     */
    public UpdateTask consume(ParseTask taskMessage, boolean send) {
        UUID taskId = taskMessage.getId();
        Comics comic = taskMessage.getComic();
        Page lastPage = taskMessage.getPage();
        logger.info("task id with = {}, comic = {}, page with = {}", taskId, comic.toString(), lastPage.toString());

        Page prevPage;
        try {
            prevPage = parse(comic, lastPage);
        } catch (IOException e) {
            logger.error("error parsing message ", e);
            return new UpdateTask(comic, null);
        }

        List<Page> pages = new ArrayList<>();
        pages.add(prevPage);

        while (prevPage.getNextUrl() != null && checkLimit(pages.size())) {
            try {
                prevPage = parse(comic, prevPage);
            } catch (IOException e) {
                logger.error("error parsing message ", e);
                break;
            }
            pages.add(prevPage);
        }

        UpdateTask updateTask = new UpdateTask(comic, pages);
        if (send) {
            updateComicsSender.send(updateTask);
        }
        return updateTask;
    }

    private Page parse(Comics comic, Page prevPage) throws IOException {
        return pageParsers.get(comic.getParserCode())
                .parsePage(comic, prevPage);
    }

    private boolean checkLimit(int count) {
        return count < maxPerTask || maxPerTask <= 0;
    }
}
