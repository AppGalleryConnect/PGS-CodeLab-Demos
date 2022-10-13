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

import { Client, Room } from './SDK/GOBE';

const { ccclass } = cc._decorator;

@ccclass
export default class Global {
  public static openId = String(Date.now()); // 未登录状态使用当前时间戳作为openId
  public static playerId: string = null;
  public static nickName = '未登录';
  public static playerIconUrl = '';
  public static gameWin = false;

  // GameOBE project info
  public static appId = '105109409';
  public static clientId = '718776077425573888';
  public static clientSecret = 'AB6AC0CB1F7CA34195C2E44487C50F32132548BA64291C20B536D1D7A39A33EA';

  // GameOBE
  public static client: Client = null;
  public static room: Room = null;
}
