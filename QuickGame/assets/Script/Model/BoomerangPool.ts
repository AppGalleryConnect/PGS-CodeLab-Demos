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

const { ccclass, property } = cc._decorator;

@ccclass
export default class BoomerangPool extends cc.Component {
  @property(cc.Integer)
  public contentCount = 10;

  @property(cc.Prefab)
  public boomerangPrefab: cc.Prefab = null;

  boomerangPool: cc.NodePool;

  start() {
    this.boomerangPool = new cc.NodePool();
    for (let index = 0; index < this.contentCount; index++) {
      const boomerang = cc.instantiate(this.boomerangPrefab);
      boomerang.getComponent('Boomerang').boomerangPool = this;
      this.boomerangPool.put(boomerang);
    }
  }

  // 获取对象
  createBoomerang() {
    let boomerang = null;
    if (this.boomerangPool.size() > 0) {
      boomerang = this.boomerangPool.get();
    } else {
      boomerang = cc.instantiate(this.boomerangPrefab);
    }
    return boomerang;
  }

  // 返回对象
  freeBoomerang(boomerang) {
    this.boomerangPool.put(boomerang);
  }
}
