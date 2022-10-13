/**
 * Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import Global from '../Global';
import { iFrameData } from '../Model/iFrameData';
import Player from '../Model/Player';
import { RecvFrameMessage } from '../SDK/GOBE';

const { ccclass, property } = cc._decorator;

@ccclass
export default class Game2 extends cc.Component {
  @property(cc.Node)
  playerNode1: cc.Node = null;

  @property(cc.Node)
  playerNode2: cc.Node = null;

  @property(cc.Node)
  gameOverNode: cc.Node = null;

  @property(cc.Node)
  winnerNode: cc.Node = null;

  @property(cc.Node)
  loserNode: cc.Node = null;

  @property(cc.Label)
  ID_value: cc.Label = null;

  @property(cc.Label)
  NickName_value: cc.Label = null;

  private isGameOver = false;

  private player1: Player;

  private player2: Player;

  // LIFE-CYCLE CALLBACKS:

  onLoad() {
    cc.director.getPhysicsManager().enabled = true;
    cc.director.getPhysicsManager().gravity = cc.v2(0, -320);
  }

  start() {
    // 注册广播帧消息回调
    this.onRecvFrame();

    this.player2 = this.playerNode2.getComponent('Player');
    this.player1 = this.playerNode1.getComponent('Player');

    this.player1.gameResCallback = this.gameResultCallback.bind(this);
    this.player2.gameResCallback = this.gameResultCallback.bind(this);
  }

  /**
   *
   * @param winnerId P1:1,P2:2
   */
  gameResultCallback(winnerId) {
    this.isGameOver = true;
    this.ID_value.string = Global.openId;
    this.NickName_value.string = Global.nickName;
    this.gameOverNode.active = true;
    const isOwner: boolean = Global.playerId === Global.client.room.ownerId;
    if ((isOwner && winnerId === 1) || (!isOwner && winnerId === 2)) {
      this.winnerNode.active = true;
      this.loserNode.active = false;
    } else {
      this.winnerNode.active = false;
      this.loserNode.active = true;
    }
  }

  /**
   * 发送射击指令帧
   */
  sendFireFrame() {
    console.log("sendFireFrame")
    const data: iFrameData = {
      jumpCmd: 0,
      fireCmd: 1,
    };
    const frameData: string = JSON.stringify(data);
    Global.room.sendFrame(frameData);
  }

  /**
   * 发送起跳指令帧
   */
  sendJumpFrame() {
    console.log("sendJumpFrame")
    const data: iFrameData = {
      jumpCmd: 1,
      fireCmd: 0,
    };
    const frameData: string = JSON.stringify(data);
    Global.room.sendFrame(frameData);
  }

  /**
   * 广播帧监听
   */
  onRecvFrame() {
    Global.room.onRecvFrame((frame: RecvFrameMessage | RecvFrameMessage[]) => {
      if (frame instanceof Array) {
        if (frame && frame.length > 0) {
          frame.forEach((frameData) => {
            this.handleFrame(frameData);
          });
        }
      } else {
        this.handleFrame(frame);
      }
    });
  }

  /**
   * 接收帧处理
   * @param frameData
   */
  handleFrame(frameData: RecvFrameMessage) {
    // eslint-disable-next-line eqeqeq
    if (frameData.frameInfo != null && frameData.frameInfo.length > 0) {
      frameData.frameInfo.forEach((frameItem) => {
        const player: Player = frameItem.playerId === Global.client.room.ownerId ? this.player1 : this.player2;
        const datas: string[] = frameItem.data;
        if (datas && datas.length > 0) {
          datas.forEach((dataItem) => {
            const object: iFrameData = JSON.parse(dataItem);
            // do action
            object.jumpCmd && player.jump();
            object.fireCmd && player.fire();
          });
        }
      });
    }
  }

  /**
   * 观看广告后复活
   */
  relive() {
    this.isGameOver = false;
    this.winnerNode.active = false;
    this.loserNode.active = false;
    cc.director.loadScene('Hall');
  }

  /**
   * 比赛结束后再来一局
   */
  gameAgin() {
    // 再来一局
    this.isGameOver = false;
    this.winnerNode.active = false;
    this.loserNode.active = false;
    cc.director.loadScene('Hall');
  }

  /**
   * 比赛结束后退出游戏
   */
  closeGame() {
    cc.game.end();
  }
}
