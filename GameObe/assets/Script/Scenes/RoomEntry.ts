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

const { ccclass } = cc._decorator;

@ccclass
export default class NewClass extends cc.Component {
  creatRoom() {
    Global.client
      .createRoom({
        maxPlayers: 2,
      })
      .then((room) => {
        // 创建房间中
        Global.room = room;
        cc.log('创建房间成功，房间号为' + room.roomCode);
        cc.director.loadScene('Room');
      })
      .catch((e) => {
        // 创建房间失败
        cc.log('创建房间失败，错误信息为：' + e);
      });
  }

  goJoinRoom() {
    cc.director.loadScene('JoinRoom');
  }

  // update (dt) {}
}
