package com.example.myapp.controller;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Jack.kj@alibaba-inc.com
 * @date 2022/10/2022/10/18
 */
public class MainControllerTest {
    private final MainController mainController = new MainController();

    @Test
    public void indexReturnIndexHtmlIfValidParam() {
        String pageView = mainController.index("corpId", "openConversationId");
        assertEquals("index.html", pageView);
    }
}