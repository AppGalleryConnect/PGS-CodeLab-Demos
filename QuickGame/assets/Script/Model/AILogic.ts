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

import Game from '../Scenes/Game';
import Player from './Player';

const { ccclass, property } = cc._decorator;

@ccclass
export default class AILogic extends cc.Component {
  @property(cc.Boolean)
  AISwitch = true;

  @property(cc.Float)
  fireDuration = 3;

  @property(cc.Float)
  jumpDuration = 5;

  private player: Player;

  jumpPassTime = 0;

  firePassTime = 0;

  start() {
    this.player = this.node.getComponent('Player');
  }

  update(dt) {
    if (Game.isGameOver) {
      this.AISwitch = false;
      return;
    }

    this.jumpPassTime += dt;
    this.firePassTime += dt;
    if (this.AISwitch && this.jumpPassTime >= this.jumpDuration) {
      this.player.jump();
      this.jumpPassTime = 0;
    }
    if (this.AISwitch && this.firePassTime >= this.fireDuration) {
      this.player.fire();
      this.firePassTime = 0;
    }
  }
}
