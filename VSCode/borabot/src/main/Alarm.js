import React, {Component} from 'react';
import axios from 'axios';

import './Alarm.css'

class Alarm extends Component {
  constructor(props) {
    super(props);

    this.state = {
      alarmList: [
        // 앞단 테스트용
        // {"exchange_name":"binance","bot_name":"준형이똥쟁이","coin":"ETHbtc","sales_action":"-1","coin_intent":"0.0","coin_price":"313500.0","trans_time":"2018-08-14 18:51:01"},
        // {"exchange_name":"bitthumb","bot_name":"준형이바보","coin":"BTCeth","sales_action":"1","coin_intent":"0.0","coin_price":"0.043949","trans_time":"2018-08-14 18:50:43"},
        // {"exchange_name":"binance","bot_name":"준형이똥멍청이","coin":"ETHbtc","sales_action":"-1","coin_intent":"0.0","coin_price":"313500.0","trans_time":"2018-08-14 18:51:01"},
        // {"exchange_name":"bitthumb","bot_name":"추워","coin":"BTCeth","sales_action":"1","coin_intent":"0.0","coin_price":"0.043949","trans_time":"2018-08-14 18:50:43"},
        // {"exchange_name":"bitthumb","bot_name":"준형이똥쟁이","coin":"ETHbtc","sales_action":"-1","coin_intent":"0.0","coin_price":"313500.0","trans_time":"2018-08-14 18:51:01"},
        // {"exchange_name":"binance","bot_name":"준형이똥쟁이","coin":"BTCeth","sales_action":"1","coin_intent":"0.0","coin_price":"0.043949","trans_time":"2018-08-14 18:50:43"},
        // {"exchange_name":"bitthumb","bot_name":"준형이바보","coin":"BTCeth","sales_action":"1","coin_intent":"0.0","coin_price":"0.043949","trans_time":"2018-08-14 18:50:43"}
        // 앞단 테스트용
      ]
    };
  }

  // 알림 버튼을 누르면 서버에서 알림 정보 받아옴
  componentDidMount() { 
    axios.get('Alarm')
    .then( response => {
      // 세션 검증
      response.data === 'sessionExpired'
      ? this.sessionExpired()
      : this.setState({ alarmList: response.data })
    }) 
    .catch( response => { console.log('err\n'+response); } ); // ERROR
  }

  // 세션 유효성 검증
  sessionExpired = () => {
    alert('세션이 종료되었습니다\n다시 로그인하세요')
    window.location = '/'
  }

  render() {
    return (
      <div>
        <div class="alarm-title">
          <th>
            <h2>&emsp;BORABOT 거래 알람</h2>
          </th>
          {/* 닫기 버튼 */}
          <th class="alarm-tableButton">
            <img onClick={()=>{this.props.close()}} src={require('../img/common/btn_06.png')} style={{cursor: "pointer"}}/>
          </th>
        </div>
        <div className="alarm-content">
          <table class="alarm-table">
            <thead class="alarm-tableTitles" style={{width:'100%'}}>
              <th class="alarm-tableTitle6">시간</th>
              <th class="alarm-tableTitle6">봇 이름</th>
              <th class="alarm-tableTitle1">거래소</th>
              <th class="alarm-tableTitle2">시장</th>
              <th class="alarm-tableTitle3">매매</th>
              <th class="alarm-tableTitle4">수량</th>
              <th class="alarm-tableTitle5">가격<small>(KRW)</small></th>
            </thead>
            <tbody class="alarm-tableContents" style={{width:'100%'}}>
                { // state에 저장된 알림 목록을 map 함수 통하여 표시
                this.state.alarmList.map((l, i) => {
                  return (<tr key={i} class="alarm-tableContent">
                    <td class="alarm-tableContent6">{l.trans_time}</td>
                    <td class="alarm-tableContent6">{l.bot_name}</td>
                    <td class="alarm-tableContent1">{l.exchange_name.toUpperCase()}</td>
                    <td class="alarm-tableContent2">{l.coin.toUpperCase()}</td>
                    <td class="alarm-tableContent3">{l.sales_action === "1" ? ("매수") : ( "매도" )}</td>
                    <td class="alarm-tableContent4">{l.coin_intent}</td>
                    <td class="alarm-tableContent5">{l.coin_price}</td>
                  </tr>)
                })}
            </tbody>
          </table>
        </div>
      </div>
    );
   }
}

export default Alarm;