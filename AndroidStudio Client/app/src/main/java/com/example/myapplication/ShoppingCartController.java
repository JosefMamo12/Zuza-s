package com.example.myapplication;

public class ShoppingCartController {
    ShoppingCartModel shoppingCartModel;
    ShoppingCartView shoppingCartView;

    public ShoppingCartController(ShoppingCartModel shoppingCartModel, ShoppingCartView shoppingCartView){
        this.shoppingCartModel = shoppingCartModel;
        this.shoppingCartView = shoppingCartView;
    }
}
