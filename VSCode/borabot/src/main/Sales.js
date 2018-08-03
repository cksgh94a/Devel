import React, { Component } from 'react';
import axios from 'axios';
import { connect } from 'react-redux';

import DayPickerInput from 'react-day-picker/DayPickerInput';
import 'react-day-picker/lib/style.css';

import { setSales } from '../reducers/sales';

import './Sales.css';

// 시간 선택 리스트
const hourList = []
for(var i=1;i<=24;i++) hourList.push(i-1)  

class Sales extends Component {
  constructor(props) {
    super(props);

    this.state = {
      selectedDay: new Date() // 선택한 날짜
    };
  }

  // 날짜 변경 핸들
  handleDayChange = (day) => {
    this.setState({ selectedDay: day });
  }

  // 거래 설정 값들의 인덱스 저장 (차트 표시용 인덱스)
  handleIndex = () => {
    this.props.onSetSales({
      sales: true,
      exchangeIndex: document.getElementById('salesExchange').selectedIndex,
      baseIndex: document.getElementById('salesBase').selectedIndex,
      coinIndex: document.getElementById('salesCoin').selectedIndex,
      intervalIndex: document.getElementById('salesInterval').selectedIndex
    })
  }

  // 거래 시작 버튼 클릭 시
  handleStartbtn = () => {
    // 봇 이름 검증
    if(document.getElementById('botname').value === ''){
      alert('😆 당신의 지갑을 풍족하게 해줄 귀여운 봇의 이름을 지어주세요 😆')
      return
    }

    // 종료일 검증
    const { selectedDay } = this.state
    // 종료일 문자열 생성
    var endDate = selectedDay.getFullYear()+'-'+
      ("0"+(selectedDay.getMonth()+1)).slice(-2)+'-'+
      ("0"+selectedDay.getDate()).slice(-2)+'T'+
      ("0"+document.getElementById('endHour').value).slice(-2)+':00:00.000'
    var now = new Date();
    if(new Date(endDate) - now < 0){
      alert('😆 거래 종료를 과거에 할 순 없어요 😆')
      return
    }
    
    // 시작일 문자열 생성
    var startDate = now.getFullYear()+'-'+
      ("0"+(now.getMonth()+1)).slice(-2)+'-'+
      ("0"+now.getDate()).slice(-2)+'T'+
      ("0"+now.getHours()).slice(-2)+':'+
      ("0"+now.getMinutes()).slice(-2)+':'+
      ("0"+now.getSeconds()).slice(-2)+'.000'

    // 거래 확인 메세지
    let alertMsg = document.getElementById('botname').value + '\n' + 
      document.getElementById('salesExchange').value+ '\n' +
      document.getElementById('salesBase').value+ '\n' +
      document.getElementById('salesCoin').value+ '\n' +
      document.getElementById('salesInterval').value+ '\n' +
      document.getElementById('strategy').value+ '\n' +
      document.getElementById('buyingSetting').value+ '\n' +
      document.getElementById('sellingSetting').value+ '\n' +
      endDate+ '\n' +
      '\n이 맞습니까?';

    // 최종 확인 후 거래 시작 (서버에 거래 정보 전송)
    if(window.confirm(alertMsg)){
      axios.post( 
        'TradeMain', 
        'status='+true+
        '&botname='+document.getElementById('botname').value+
        '&exchange='+document.getElementById('salesExchange').value+
        '&base='+document.getElementById('salesBase').value+
        '&coin='+document.getElementById('salesCoin').value+
        '&interval='+this.props.intervalList[this.props.sales.intervalIndex].value+
        '&strategyName='+document.getElementById('strategy').value+
        '&buyingSetting='+document.getElementById('buyingSetting').value+
        '&sellingSetting='+document.getElementById('sellingSetting').value+
        '&startDate='+startDate+
        '&endDate='+endDate,
        { 'Content-Type': 'application/x-www-form-urlencoded' }
      )
      alert('거래가 시작되었습니다.')
    } else alert('취소되었습니다.')
  }

  render() {
    const { exchangeList, intervalList, strategyList } = this.props
    const { exchangeIndex, baseIndex } = this.props.sales

    return (
      <div style={{color:"grey"}}>        
        <h4 className="Sales-color">Sales configuration</h4>
        <input placeholder="이름" id="botname"/><br/>
        거래소 : <select id="salesExchange" onChange={this.handleIndex}>
          {exchangeList.map((exchange, index) => {
            return (<option key={index} > {exchange.key} </option>)
          })
          }
        </select><br/>
        기축통화 : <select id="salesBase" onChange={this.handleIndex}>
          {exchangeList[exchangeIndex].value.baseList.map((base, i) => {
            return (<option key={i}> {base} </option>)
          })}
        </select><br/>
        코인 : <select id="salesCoin" onChange={this.handleIndex}>
          {exchangeList[exchangeIndex].value.coin[baseIndex].list.map((coin, i) => {
            return (<option key={i}> {coin} </option>)
          })}
        </select><br/>
        거래 간격 : <select id="salesInterval" onChange={this.handleIndex}>
          {intervalList.map((int, i) => {
            return (<option key={i}> {int.key} </option>)
          })}
        </select><br/>
        전략 : <select id="strategy">
          {strategyList.map((s, i) => {
            return (<option key={i}> {s.name} </option>)
          })}
        </select><br/>        
        구매 설정 : <select id="buyingSetting">
            <option> buyAll </option>
        </select><br/>
        판매 설정 : <select id="sellingSetting">
            <option> sellAll </option>
        </select><br/>
        종료일 : 
        <DayPickerInput onDayChange={this.handleDayChange} />
        <select id="endHour">
          {hourList.map((e, i) => {
            return (<option key={i} selected={e === new Date().getHours()}> {e} </option>)
          })}
        </select>시<br/>
        <button onClick={this.handleStartbtn}>거래 시작</button>
      </div>
    );
  }

}

let mapDispatchToProps = (dispatch) => {
  return {
    onSetSales: (value) => dispatch(setSales(value))
  }
}

let mapStateToProps = (state) => {
  return {
    strategyList: state.strategy.strategyList,
    exchangeList: state.exchange.exchangeList,
    intervalList: state.exchange.intervalList,

    sales: state.sales
  };
}

Sales = connect(mapStateToProps, mapDispatchToProps)(Sales);

export default Sales;