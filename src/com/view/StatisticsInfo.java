package com.view;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

public class StatisticsInfo implements Initializable
{
    // 테이블 뷰에 사용할 리스트
    private ObservableList<String> benefit_list;
    private ObservableList<String> rsv_list;
    private ObservableList<String> cancel_list;
    
    private String start_date; // 시작, 종료 날짜 저장
    private String end_date;
    
    @FXML
    private AnchorPane ap_statistics_main;
    
    @FXML
    private TableView<String> tv_benefit_rate;
    
    @FXML
    private TableColumn<String, String> tc_theater;
    
    @FXML
    private TableColumn<String, String> tc_benefit;
    
    @FXML
    private TableView<String> tv_rsv_rate;
    
    @FXML
    private TableColumn<String, String> tc_movie_rsv;
    
    @FXML
    private TableColumn<String, String> tc_rsv;
    
    @FXML
    private TableView<String> tv_cancel_rate;
    
    @FXML
    private TableColumn<String, String> tc_movie_cancel;
    
    @FXML
    private TableColumn<String, String> tc_cancel;
    
    @FXML
    private DatePicker dp_start;
    
    @FXML
    private DatePicker dp_end;
    
    // 초기화
    @Override
    public void initialize(URL arg0, ResourceBundle arg1)
    {
        try
        {
            dp_start.setValue(LocalDate.now()); // 현재날짜로 지정
            dp_end.setValue(LocalDate.now());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 포맷터 설정
            start_date = dp_start.getValue().format(formatter) + " 00:00:00.0"; // 시작, 종료 날짜 설정
            end_date = dp_end.getValue().format(formatter) + " 23:59:00.0";
            
            benefit_list = FXCollections.observableArrayList();
            rsv_list = FXCollections.observableArrayList();
            cancel_list = FXCollections.observableArrayList();
            
            initList();
            
            tc_theater.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().split("/")[0]));
            tc_benefit.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().split("/")[1]));
            tv_benefit_rate.setItems(benefit_list);
            
            tc_movie_rsv.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().split("/")[0]));
            tc_rsv.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().split("/")[1]));
            tv_rsv_rate.setItems(rsv_list);
            
            tc_movie_cancel.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().split("/")[0]));
            tc_cancel.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().split("/")[1]));
            tv_cancel_rate.setItems(cancel_list);
        }
        catch (Exception e)
        {
            
            e.printStackTrace();
        }
    }
    
    // date picker 선택 시
    @FXML
    void selectEndDate(ActionEvent event)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        end_date = dp_end.getValue().format(formatter) + " 23:59:00.0";
    }
    
    @FXML
    void selectStartDate(ActionEvent event)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        start_date = dp_start.getValue().format(formatter) + " 00:00:00.0";
    }
    
    // 통계 검색
    @FXML
    void search(ActionEvent event)
    {
        initList();
    }
    
    // 리스트 초기화
    private void initList()
    {
        try
        {
            benefit_list.clear();
            rsv_list.clear();
            cancel_list.clear();
            
            mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_STATISTICS_VIEW + "`" + start_date + "`" + end_date);
            
            while (true)
            {
                String packet = mainGUI.readLine();
                String packetArr[] = packet.split("`"); // 패킷 분할
                String packetType = packetArr[0];
                String packetCode = packetArr[1];
                
                if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_STATISTICS_VIEW))
                {
                    String result = packetArr[2];
                    
                    switch (result)
                    {
                        case "1":
                        {
                            String statistics_list = packetArr[3];
                            String statisticsArr[] = statistics_list.split("\\{");
                            String benefirArr[] = statisticsArr[0].split("\\|");
                            String rsvArr[] = statisticsArr[1].split("\\|");
                            String cancelArr[] = statisticsArr[2].split("\\|");
                            
                            benefit_list.addAll(benefirArr);
                            rsv_list.addAll(rsvArr);
                            cancel_list.addAll(cancelArr);
                            return;
                        }
                        case "2":
                        {
                            mainGUI.alert("오류", "통계 정보를 불러오는데 실패했습니다.");
                            return;
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            
            e.printStackTrace();
        }
        
    }
    
}
