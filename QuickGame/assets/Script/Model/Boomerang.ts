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

import BoomerangPool from './BoomerangPool';

const { ccclass, property } = cc._decorator;

@ccclass
export default class Boomerang extends cc.Component {
  @property(cc.Sprite)
  Boomerang: cc.Sprite = null;

  @property(cc.Float)
  public flySpeed = 2000;

  boomerangPool: BoomerangPool;

  flyDirection = 1;

  rBody: cc.RigidBody;

  onLoad() {
    this.rBody = this.node.getComponent(cc.RigidBody);
  }

  init(): void {
    this.flyDirection = this.node.parent.name === 'p1' ? 1 : -1;
    this.node.x = this.flyDirection * 300;
    this.rBody.linearVelocity = new cc.Vec2(this.flySpeed * this.flyDirection, 0);
  }

  onBeginContact(contact, selfCollider: cc.PhysicsCollider, otherCollider: cc.PhysicsCollider) {
    this.rBody.linearVelocity = new cc.Vec2(0, 0);
    this.rBody.angularVelocity = 0;
    this.boomerangPool.freeBoomerang(this.node);
    cc.log('飞镖：探测碰撞到玩家' + otherCollider.tag);
  }

  protected update(): void {
    if (this.node.x >= 3000 || this.node.x <= -3000) {
      this.boomerangPool.freeBoomerang(this.node);
    }
  }
}
