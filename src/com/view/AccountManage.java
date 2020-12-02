package com.view;


import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AccountManage {

	@FXML
    private TableView<?> tv_account;

    @FXML
    private TableColumn<?, ?> tc_bank;

    @FXML
    private TableColumn<?, ?> tc_account_num;

    @FXML
    private TableColumn<?, ?> tc_money;

    @FXML
    private MenuButton mb_bank;

    @FXML
    private TextField tf_bank;

    @FXML
    private Button btn_change;

    @FXML
    void getChange(ActionEvent event) {

    }
}