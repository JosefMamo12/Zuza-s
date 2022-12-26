package com.example.myapplication;

import junit.framework.TestCase;

import org.junit.Assert;

public class MenuItemAdditionTest extends TestCase {


    public void testAddItem()
    {
        MenuItemAddition testPage = new MenuItemAddition();
        Assert.assertFalse(testPage.addItem(new MenuItemModel("ex", "ex", "1.00"), "non-existent"));
    }

    public void testOnClick()
    {
    }
}