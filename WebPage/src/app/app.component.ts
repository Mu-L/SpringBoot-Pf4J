import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {NzMessageService} from "ng-zorro-antd/message";
import {NzUploadChangeParam} from "ng-zorro-antd/upload";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.less']
})
export class AppComponent implements OnInit{
  title = 'WebPage';
  dataSet = [];
  greatStr = '点击上方按钮返回 结果';
  resultJson = '';
  orderJson = `{
    "count": 1,
    "buyer": 10,
    "price": 12.3,
    "address": "abcde"
}
  `;

  constructor(private http: HttpClient, private message: NzMessageService) {
  }

  ngOnInit(): void {
    this.http.get('/api/pluginInfo').subscribe((value: any) => {
      this.dataSet = [...value.data];
    });
  }

  testPlanA() {
    this.http.get('/api/great', {
      responseType: 'text'
    }).subscribe((value: any) => {
      this.greatStr = value;
    });
  }

  testPlanB() {
    try {
      var parse = JSON.parse(this.orderJson);
      this.http.post('/api/order', parse).subscribe((value: any) => {
        this.resultJson = JSON.stringify(value, null, '\t');
      });
    } catch (e){
      console.log(e);
      this.message.error('Json 格式错误');
      return;
    }
  }

  start(i) {
    this.http.get('/api/start', {
      responseType: 'text',
      params: {
        id: i
      }
    }).subscribe((value: any) => {
      if (value !== 'STARTED') {
        this.message.error('启用失败： ' + value);
      }else {
        this.ngOnInit();
      }
    });
  }

  stop(i) {
    this.http.get('/api/stop', {
      responseType: 'text',
      params: {
        id: i
      }
    }).subscribe((value: any) => {
      if (value !== 'STOPPED') {
        this.message.error('启用失败： ' + value);
      }else {
        this.ngOnInit();
      }
    });
  }

  handleChange(info: NzUploadChangeParam) {
    if (info.file.status === 'done') {
      console.log(info);
      if (!info.file.response.result) {
        this.message.error("更新失败: " + info.file.response.msg);
      }else {
        this.message.success("更新成功");
      }
    }
  }
}
