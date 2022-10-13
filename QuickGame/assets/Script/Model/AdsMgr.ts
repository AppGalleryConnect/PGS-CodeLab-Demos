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

const { ccclass } = cc._decorator;

@ccclass
export default class AdsMgr {
  // 预加载操作激励视频，创建视频对象，加载视频load，监听调用onload，监听关闭onclose
  public static Instance: AdsMgr = null;
  private rewardedVideoAd;

  static getInstance() {
    if (!AdsMgr.Instance) {
      AdsMgr.Instance = new AdsMgr();
    }
    return AdsMgr.Instance;
  }

  loadRewardedVideoAd(callback: () => void) {
    cc.log('加载广告中');
    // 创建广告组件
    this.rewardedVideoAd = qg.createRewardedVideoAd({
      adUnitId: 'testx9dtjwj8hp',
      success: () => {
        cc.log('ads : createRewardedVideoAd success');
      },
      fail: (data, code) => {
        cc.log('ads : createRewardedVideoAd fail: ' + data + ',' + code);
      },
      complete: () => {
        cc.log('ads : createRewardedVideoAd complete');
      },
    });

    this.rewardedVideoAd.onLoad(() => {
      cc.log('ads :ad loaded.');
    });

    this.rewardedVideoAd.onError((e) => {
      cc.error('load ad error:' + JSON.stringify(e));
    });
    this.rewardedVideoAd.onClose((res) => callback(res));

    // 预加载激励视频
    this.rewardedVideoAd.load();
  }
  // 由于激励广告要求预加载，可在进入游戏时立即触发上述逻辑。在onLoad触发成功回调时，可以展示视频广告组件。
  // 玩家每次点击视频按钮时调用rewardedVideoAd.show()播放广告。在播放期间或者关闭视频前调用rewardedVideoAd.load()请求下一次广告
  showRewardedVideoAd() {
    this.rewardedVideoAd.show();
    this.rewardedVideoAd.load();
  }

  destroyRewardedVideoAd() {
    this.rewardedVideoAd.offLoad();
    this.rewardedVideoAd.offClose();
    this.rewardedVideoAd.destroy();
  }

  destroy() {
    this.destroy();
  }
}
