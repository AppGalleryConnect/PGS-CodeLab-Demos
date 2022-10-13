declare const enum AutoFrame {
    AUTO_FRAME_OFF = 0,
    AUTO_FRAME_ON = 1
}

/**
 * 可匹配房间列表信息
 * @param rooms - 单次请求的房间列表
 * @param count - 所有房间的总数
 * @param offset - 偏移量，作为下一次查询请求的入参
 * @param hasNext - 是否有下一页 0：无 1：有
 * @public
 */
export declare interface AvailableRoomsInfo {
    rooms: RoomInfo[];
    count: number;
    offset: string | number;
    hasNext: 0 | 1;
}

/**
 * Base 类
 * @public
 */
export declare class Base {
    protected get state(): StateCode;
    protected get serverEventCode(): ServerEventCode;
    protected get appId(): string;
    protected get openId(): string;
    protected get serviceToken(): string;
    get playerId(): string;
    get lastRoomId(): string;
    get roomId(): string;
    get groupId(): string;
    protected constructor();
    protected setState(state: StateCode): void;
    protected setServerEvent(code: ServerEventCode, param?: string): void;
    protected setAppId(id: string): void;
    protected setOpenId(id: string): void;
    protected setServiceToken(token: string): void;
    protected setPlayerId(id: string): void;
    protected setLastRoomId(roomId: string): void;
    protected setRoomId(id: string): void;
    protected setGroupId(id: string): void;
    protected onServerEventChange(latter: ServerEvent, former: ServerEvent): void;
}

declare interface BaseResponse {
    rtnCode: number;
    msg?: string;
}

declare interface CancelMatchResponse extends BaseResponse {
    roomId?: string;
}

/**
 * 客户端类
 * @public
 */
export declare class Client extends Base {
    private _auth;
    private _room;
    private _group;
    private _pollInterval;
    private _isMatching;
    private _loginTimestamp;
    onMatch: {
        (this: any, cb: (onMatchResponse: OnMatchResponse) => any): EventEmitter<(onMatchResponse: OnMatchResponse) => any>;
        emit(onMatchResponse: OnMatchResponse): void;
        off(cb: (onMatchResponse: OnMatchResponse) => any): void;
        clear(): void;
    };
    /**
     * 获取对应房间实例
     * @readonly
     */
    get room(): Room | null;
    /**
     * 获取对应队伍实例
     * @readonly
     */
    get group(): Group | null;
    /**
     * 获取玩家登录时间戳
     * @readonly
     */
    get loginTimestamp(): number;
    /**
     * 销毁客户端
     */
    destroy(): Promise<void>;
    /**
     * 创建客户端
     * @param config - 创建客户端参数
     */
    constructor(config: ClientConfig);
    /**
     * 初始化客户端
     * @remarks 必须先初始化客户端，才能创建/加入/匹配房间
     */
    init(): Promise<Client>;
    /**
     * 创建房间
     * @remarks 创建成功也意味着加入了该房间
     * @param createRoomConfig - 房间信息参数
     * @param playerConfig - 玩家信息参数
     */
    createRoom(createRoomConfig: CreateRoomConfig, playerConfig?: PlayerConfig): Promise<Room>;
    /**
     * 创建队伍
     * @remarks 创建成功也意味着加入了该队伍
     * @param groupConfig - 队伍信息参数
     * @param playerConfig - 玩家信息参数
     */
    createGroup(groupConfig: CreateGroupConfig, playerConfig?: PlayerConfig): Promise<Group>;
    /**
     * 加入房间
     * @param roomIdentity - 房间身份标识（房间Id或者房间Code）
     * @param playerConfig - 玩家信息参数
     */
    joinRoom(roomIdentity: string, playerConfig?: PlayerConfig): Promise<Room>;
    /**
     * 根据队伍ID加入队伍
     * @param groupId - 队伍 ID
     * @param playerConfig - 玩家信息参数
     */
    joinGroup(groupId: string, playerConfig?: PlayerConfig): Promise<Group>;
    /**
     * 离开房间
     */
    leaveRoom(): Promise<Client>;
    /**
     * 根据lastRoomId请求离开房间
     */
    private leave;
    /**
     * 解散房间
     * @remarks 房主才能解散房间
     */
    dismissRoom(): Promise<Client>;
    /**
     * 离开队伍
     */
    leaveGroup(): Promise<Client>;
    /**
     * 解散队伍
     * @remarks 队长才能解散队伍
     */
    dismissGroup(): Promise<Client>;
    /**
     * 删除队伍实例
     * @remarks 队员收到队伍解散通知后清空本地队伍信息
     */
    removeGroup(): void;
    /**
     * 重置房间起始帧ID，调用场景：在加入房间后，开始游戏前调用
     * @param frameId - 帧ID
     */
    resetRoomFrameId(frameId: number): Promise<Client>;
    /**
     * 获取可匹配房间列表
     */
    getAvailableRooms(getAvailableRoomsConfig: GetAvailableRoomsConfig): Promise<AvailableRoomsInfo>;
    /**
     * 房间匹配
     * @param matchRoomConfig - 房间匹配参数
     * @param playerConfig - 玩家信息参数
     */
    matchRoom(matchRoomConfig: MatchRoomConfig, playerConfig?: PlayerConfig): Promise<Room>;
    /**
     * 在线匹配
     * @param matchPlayerConfig - 在线匹配参数
     * @param playerConfig - 玩家信息参数
     */
    matchPlayer(matchPlayerConfig: MatchPlayerConfig, playerConfig?: PlayerConfig): Promise<MatchResponse>;
    /**
     * 组队匹配
     * @param matchGroupConfig - 组队匹配参数
     * @param playerConfig - 玩家信息参数
     * @remarks 队长才能主动发起组队匹配，队员通过广播通知被动调起组队匹配
     */
    matchGroup(matchGroupConfig: MatchGroupConfig, playerConfig?: PlayerConfig): Promise<MatchResponse>;
    /**
     * 匹配查询
     * @param playerConfig - 玩家信息参数
     * @remarks 调用匹配接口后，自动触发匹配查询轮询，获得结果后触发onMatch监听
     */
    matchQuery(playerConfig?: PlayerConfig): void;
    /**
     * 取消匹配
     * @remarks 组队匹配模式中，当前只有队长可以取消匹配
     */
    cancelMatch(): Promise<CancelMatchResponse>;
    /**
     * 移除所有事件监听
     */
    removeAllListeners(): void;
    private checkInit;
    private checkCreateOrJoin;
    private checkGroupCreateOrJoin;
    private checkLeaveOrdismiss;
    private checkLastRoomId;
    private checkGroupLeaveOrdismiss;
    private checkCreateRoomConfig;
    private checkCreateGroupConfig;
    private checkJoinRoomConfig;
    private checkMatching;
}

/**
 * Client 类构造方法参数
 * @param clientId - 客户端ID
 * @param clientSecret - 客户端密钥
 * @param appId - 应用ID
 * @param platform - 游戏平台
 * @param cerPath - platform为CC_ANDROID时，需要填该字段，值为cer证书路径
 * @param openId - 玩家ID
 * @param createSignature - 签名函数
 * @param accessToken - 开发者accessToken
 * @public
 */
export declare interface ClientConfig {
    clientId: string;
    openId: string;
    appId: string;
    platform?: PlatformType;
    cerPath?: string;
    clientSecret?: string;
    createSignature?: CreateSignature;
    accessToken?: string;
}

/**
 * 创建队伍方法参数
 * @param maxPlayers - 队伍最大支持人数
 * @param groupName - 队伍名称
 * @param customGroupProperties - 队伍自定义属性
 * @param isLock - 是否禁止加入 0:不禁止 1:禁止 默认0
 * @param isPersistent - 是否持久化 0:不持久化 1:持久化 默认0
 * @public
 */
export declare interface CreateGroupConfig {
    maxPlayers: number;
    groupName?: string;
    customGroupProperties?: string;
    isLock?: number;
    isPersistent?: number;
}

/**
 * 创建房间方法参数
 * @param maxPlayers - 房间最大支持人数
 * @param isPrivate - 是否私有
 * @param roomType - 房间类型
 * @param roomName - 房间名称
 * @param matchParams - 房间匹配属性
 * @param customRoomProperties - 房间自定义属性
 * @public
 */
export declare interface CreateRoomConfig {
    maxPlayers: number;
    isPrivate?: number;
    roomType?: string;
    roomName?: string;
    matchParams?: Record<string, string>;
    customRoomProperties?: string;
}

/**
 * 签名函数
 * @public
 */
export declare type CreateSignature = () => Promise<Signature>;

/**
 * 错误码
 * @public
 */
export declare const enum ErrorCode {
    COMMON_OK = 0,
    COMMON_ERR = -1,
    COMMON_INVALID_TOKEN = 2,
    COMMON_REQUEST_PARAM_ERR = 1001,
    DATABASE_OPERATION_ERR = 1002,
    DCS_OPERATION_ERR = 1003,
    FAILED_TO_VERIFY_THE_INTERFACE_SIGNATURE = 1011,
    REPEAT_RUQUEST = 4003,
    TOKEN_AUTH_FAILED = 100103,
    SERVICE_NOT_ENABLED = 100104,
    PLAYER_INFO_MIASSING = 100105,
    PROJECT_NOT_EXIST = 100108,
    QUERY_APP_INFO_FROM_AGC = 100112,
    APP_NOT_BELONG_CURRENT_PROJECT = 100113,
    INVALID_APP_SIGNATURE_VERIFICATION_PARAMETER = 100114,
    FAILED_TO_VERIFY_THE_APP = 100115,
    ROOM_PLAYER_NOT_IN_ROOM = 101101,
    PLAYER_AND_ROOM_MISMATCH = 101102,
    ROOM_INFO_NOT_EXIST = 101103,
    CANNOT_DESTORY_ROOM_IN_GAME = 101104,
    PLAYER_NOT_IN_CURRENT_ROOM = 101105,
    PLAYER_ALREADY_IN_ANOTYER_ROOM = 101106,
    PLAYERS_EXCEEDS_ROOM_MAX = 101107,
    PLAYER_INFO_NOT_EXIST = 101108,
    ROOM_OWNER_AND_PLAYER_MISMATCH = 101109,
    GENERATE_SECURE_RANDOM_NUM_FAILED = 101110,
    GET_SIGNATURE_INFO_FAILED = 101111,
    CREATE_GRPC_CHANNEL_FAILED = 101112,
    MAXPLAYER_TOO_LARGE = 101113,
    ROOM_STARTED_FRAME_SYNC = 101114,
    ROOM_STOPPED_FRAME_SYNC = 101115,
    OTHER_PLAYER_OFFLINE = 101116,
    INVALID_ROOM = 101117,
    FRAME_SYNC_OPERATION_IS_INVALID_ROOM_STATE = 101118,
    FRAME_SYNC_OPERATION_IS_INVALID_PLAYER_STATE = 101119,
    INVALID_ROOM_STATUS = 101120,
    REPEATS_PLAYER_ID = 101121,
    PARSE_TIME_FAILED = 101122,
    TOO_MANY_MATCHING_PARAMETERS = 101123,
    PLAYER_STATUS_CONNOT_MODIFY = 101124,
    ROOM_OWNER_STATUS_CANNOT_BE_KICKED = 101125,
    PLAYER_INFO_CANNOT_EMPTY = 101126,
    SOME_PLAYER_DADABASES_NOT_EXIST = 101127,
    PLAYERS_NUMBER_EXCEEDS_THE_LIMIT = 101128,
    PLAYER_NOT_LOGGED_IN = 101129,
    THE_PLAYER_ALREADY_IN_THE_ROOM = 101130,
    ROUTE_INFO_CANNOT_BE_FOUND = 101131,
    PLAYER_NOT_IN_CURRENT_GROUP = 101201,
    GROUP_NOT_EXIST = 101202,
    PLAYERS_ALREADY_IN_OTHER_GROUP = 101203,
    THE_GROUP_CONNOT_BE_DISBANDED = 101204,
    THE_PLAYER_NOT_CURRENT_GROUP_LEADER = 101205,
    CURRENT_GROUP_IS_LOCKED = 101206,
    CURRENT_GROUP_IS_FULL = 101207,
    NEW_PLAYER_NOT_IN_GROUP = 101208,
    ROOM_NOT_BEGIN_FRAME_SYNC = 102003,
    PLAYER_NOT_IN_ROOM = 102005,
    ROOM_NOT_EXIST = 102008,
    PRASE_MESSAGE_FAILED = 102009,
    UNSUPPORTED_MESSAGE_TYPE = 102013,
    REQUEST_FRAME_NUMBER_OVERRUN = 102014,
    INVALID_MESSAGE = 102016,
    NO_LEGITIMATE_TARGET_PLAYER = 102017,
    INVALID_MESSAGE_LENGTH = 102018,
    LOGIN_BUSY = 103001,
    LOGIN_AUTH_FAIL = 103002,
    CLIENT_TRAFFIC_CONTROL = 103003,
    NOT_LOGGED_IN = 103004,
    EXCEED_MAX_CONNECTIONSS = 103006,
    ROOM_MATCH_FAILED = 104101,
    ROOM_MATCHING = 104102,
    ROOM_MATCH_TIMEOUT = 104103,
    PLAYER_MATCH_FAILED = 104201,
    PLAYER_MATCHING = 104202,
    PLAYER_MATCH_TIMEOUT = 104203,
    PLAYER_MATCH_CANCEL_NO_PERMISSION = 104204,
    PLAYER_MATCH_CANCELED = 104205,
    PLAYER_MATCH_CANCEL_WHEN_SUCCESS = 104206,
    PLAYER_MATCH_GET_RULE_FAIL = 104207,
    PLAYER_MATCH_ROOM_IS_NULL = 104208,
    PLAYER_MATCH_INVALID_TEAM = 104209,
    PLAYER_MATCH_PLAYER_NOT_IN_MATCHING = 104211,
    SDK_NOT_IN_GROUP = 80001,
    SDK_GROUP_NAME_TOO_LONG = 80002,
    SDK_NO_PERMISSION_UPDATE_GROUP = 80003,
    SDK_IN_GROUP = 80004,
    SDK_UNINIT = 90001,
    SDK_NOT_IN_ROOM = 90002,
    SDK_IN_ROOM = 90003,
    SDK_NOT_ROOM_OWNER = 90004,
    SDK_NOT_IN_FRAME_SYNC = 90005,
    SDK_IN_FRAME_SYNC = 90006,
    SDK_INVALID_ROOM_IDENTITY = 90007,
    SDK_IN_MATCHING = 90008,
    SDK_NOT_IN_MATCHING = 90009,
    SDK_IN_REQUESTING = 90010,
    SDK_GROUP_MEMBERS_ERROR = 90011,
    SDK_HTTP_REQUEST_ERROR = 90012,
    SDK_ROOM_NAME_TOO_LONG = 10001,
    SDK_AUTO_REQUEST_FRAME_FAILED = 10002,
    SDK_FRAME_ID_RANGE_ERROR = 10003
}

/**
 * 事件触发器
 * @public
 */
export declare class EventEmitter<T extends (...args: any[]) => any> {
    handlers: Array<T>;
    on(handler: T): this;
    emit(...args: FunctionParam<T>): void;
    off(handler: T): void;
    clear(): void;
}

/**
 * 附加信息
 * @public
 */
export declare interface FrameExtInfo {
    seed: number;
}

/**
 * 帧数据信息
 * @public
 */
export declare interface FrameInfo extends FramePlayerInfo {
    data: string[];
    timestamp: number;
}

/**
 * 帧数据玩家信息
 * @public
 */
export declare interface FramePlayerInfo {
    playerId: string;
}

/**
 * 函数参数类型
 * @public
 */
export declare type FunctionParam<T> = T extends (...args: infer P) => any ? P : never;

/**
 * 获取可匹配房间列表参数
 * @param roomType - 房间类型
 * @param offset - 偏移量，使用房间的createTime作为每次请求的标记，第一次请求时为0
 * @param limit - 单次请求获取的房间数量，不选时服务端默认为20
 * @public
 */
export declare interface GetAvailableRoomsConfig {
    roomType?: string;
    offset?: number | string;
    limit?: number;
}

/**
 * 自定义错误类
 * @public
 */
export declare class GOBEError extends Error {
    code: number;
    constructor(code: number, message?: string);
}

/**
 * 队伍类
 * @public
 */
export declare class Group extends Base {
    onJoin: {
        (this: any, cb: (serverEvent: ServerEvent) => void): EventEmitter<(serverEvent: ServerEvent) => void>;
        emit(serverEvent: ServerEvent): void;
        off(cb: (serverEvent: ServerEvent) => void): void;
        clear(): void;
    };
    onLeave: {
        (this: any, cb: (serverEvent: ServerEvent) => void): EventEmitter<(serverEvent: ServerEvent) => void>;
        emit(serverEvent: ServerEvent): void;
        off(cb: (serverEvent: ServerEvent) => void): void;
        clear(): void;
    };
    onDismiss: {
        (this: any, cb: (serverEvent: ServerEvent) => void): EventEmitter<(serverEvent: ServerEvent) => void>;
        emit(serverEvent: ServerEvent): void;
        off(cb: (serverEvent: ServerEvent) => void): void;
        clear(): void;
    };
    onUpdate: {
        (this: any, cb: (serverEvent: ServerEvent) => void): EventEmitter<(serverEvent: ServerEvent) => void>;
        emit(serverEvent: ServerEvent): void;
        off(cb: (serverEvent: ServerEvent) => void): void;
        clear(): void;
    };
    onMatchStart: {
        (this: any, cb: (serverEvent: ServerEvent) => void): EventEmitter<(serverEvent: ServerEvent) => void>;
        emit(serverEvent: ServerEvent): void;
        off(cb: (serverEvent: ServerEvent) => void): void;
        clear(): void;
    };
    private config;
    private _player;
    private _client;
    /**
     * 队伍 ID
     */
    get id(): string;
    /**
     * 队伍名称
     */
    get groupName(): string;
    /**
     * 队伍最大人数
     */
    get maxPlayers(): number;
    /**
     * 队长 ID
     */
    get ownerId(): string;
    /**
     * 队伍自定义属性
     */
    get customGroupProperties(): string;
    /**
     * 是否禁止加入 0:不禁止 1:禁止 默认0
     */
    get isLock(): number;
    /**
     * 是否持久化 0:不持久化 1:持久化 默认0
     */
    get isPersistent(): number;
    /**
     * 队伍玩家列表
     */
    get players(): PlayerInfo[];
    /**
     * 玩家自己
     */
    get player(): Player;
    /**
     * 队伍
     * @param config - 创建客户端参数
     */
    constructor(client: Client, config: GroupInfo);
    /**
     * 队伍信息查询
     */
    query(): Promise<Group>;
    /**
     * 离开队伍
     */
    leave(): Promise<void>;
    /**
     * 解散队伍
     * @remarks 队长才能解散队伍
     */
    dismiss(): Promise<void>;
    /**
     * 更新队伍信息
     * @remarks 队长才能更新队伍信息
     * @param config - 更新队伍信息参数
     */
    updateGroup(config: UpdateGroupConfig): Promise<void>;
    /**
     * 移除队伍内玩家
     * @param playerId - 被移除出的玩家ID
     * @remarks 只有队长才有权限移除其他队员，其他队员通过onLeave收到玩家被踢通知
     */
    removePlayer(playerId: string): Promise<void>;
    private checkUpdatePermission;
    protected onServerEventChange(serverEvent: ServerEvent): Promise<void>;
    removeAllListeners(): void;
}

/**
 * 队伍信息
 * @param groupId - 队伍id
 * @param groupName - 队伍名称
 * @param maxPlayers - 最大玩家数
 * @param ownerId - 队长ID
 * @param customGroupProperties - 队伍自定义属性
 * @param isLock - 是否禁止加入 0:不禁止 1:禁止 默认0
 * @param isPersistent - 是否持久化 0:不持久化 1:持久化 默认0
 * @param players - 队伍内玩家列表
 * @public
 */
export declare interface GroupInfo {
    groupId: string;
    groupName: string;
    maxPlayers: number;
    ownerId: string;
    customGroupProperties: string;
    isLock: number;
    isPersistent: number;
    players: PlayerInfo[];
}

/**
 * 组队匹配参数
 * @param playerInfos - 带匹配规则的玩家信息列表
 * @param teamInfo - 带匹配规则队伍信息，非对称匹配场景必填，存放队伍参数
 * @param matchCode - 匹配规则编号
 * @public
 */
export declare interface MatchGroupConfig {
    playerInfos: MatchPlayerInfoParam[];
    teamInfo?: MatchTeamInfoParam;
    matchCode: string;
}

/**
 * 在线匹配参数
 * @param playerInfo - 带匹配规则的玩家信息
 * @param teamInfo - 带匹配规则队伍信息，非对称匹配场景必填，存放队伍参数
 * @param matchCode - 匹配规则编号
 * @public
 */
export declare interface MatchPlayerConfig {
    playerInfo: MatchPlayerInfoParam;
    teamInfo?: MatchTeamInfoParam;
    matchCode: string;
}

/**
 * 带匹配规则的玩家信息
 * @param matchParams - 自定义匹配参数
 * @public
 */
export declare interface MatchPlayerInfoParam {
    playerId: string;
    matchParams: Record<string, number>;
}

declare interface MatchResponse extends BaseResponse {
    roomId?: string;
}

/**
 * 房间匹配参数
 * @param matchParams - 自定义匹配参数，最多支持5条匹配规则
 * @param maxPlayers - 房间最大支持人数
 * @param roomType - 房间类型
 * @param customRoomProperties - 自定义房间属性
 * @remarks maxPlayers，roomType，customRoomProperties用于找不到匹配房间时创建房间
 * @public
 */
export declare interface MatchRoomConfig {
    matchParams: Record<string, string>;
    maxPlayers: number;
    roomType?: string;
    customRoomProperties?: string;
}

/**
 * 带匹配规则队伍信息，非对称匹配场景必填，存放队伍参数
 * @param matchParams - 自定义匹配参数
 * @public
 */
export declare interface MatchTeamInfoParam {
    matchParams: Record<string, number>;
}

declare interface OnMatchResponse extends BaseResponse {
    room?: Room;
}

export declare enum PlatformType {
    WEB = 0,
    CC_ANDROID = 1
}

/**
 * 玩家类
 * @public
 */
export declare class Player extends Base {
    customStatus?: number;
    customProperties?: string;
    constructor(customStatus?: number, customProperties?: string);
    /**
     * 更新玩家自定义状态
     */
    updateCustomStatus(status: number): Promise<Player>;
    /**
     * 更新玩家自定义属性
     */
    updateCustomProperties(customProperties: string): Promise<Player>;
}

/**
 * 玩家自定义参数
 * @param customPlayerStatus - 玩家自定义状态
 * @param customPlayerProperties - 玩家自定义属性
 * @public
 */
export declare interface PlayerConfig {
    customPlayerStatus?: number;
    customPlayerProperties?: string;
}

/**
 * 玩家信息
 * @param playerId - 玩家ID
 * @param status - 玩家状态 0：空闲；1：房间中；3：离线
 * @param customPlayerStatus - 自定义玩家状态
 * @param customPlayerProperties - 自定义玩家属性
 * @param teamId - 玩家teamId
 * @param isRobot - 是否为机器人，0：不是，1：是
 * @param robotName - 机器人名字
 * @param matchParams - 自定义匹配参数
 * @public
 */
export declare interface PlayerInfo {
    playerId: string;
    status?: number;
    customPlayerStatus?: number;
    customPlayerProperties?: string;
    teamId?: string;
    isRobot?: number;
    robotName?: string;
    matchParams?: Record<string, string>;
}

/**
 * 房间内消息码
 * @public
 */
export declare const enum Protocol {
    LOGIN = 0,
    LOGIN_ACK = 1,
    HEARTBEAT = 2,
    HEARTBEAT_ACK = 3,
    CLIENT_SEND_FRAMEDATA = 4,
    CLIENT_SEND_FRAMEDATA_ACK = 5,
    QUERY_FRAMEDATA = 6,
    QUERY_FRAMEDATA_ACK = 7,
    FRAMESYNC_STARTED = 8,
    FRAMESYNC_STOPED = 9,
    BROADCAST_FRAMEDATA = 10,
    QUERY_FRAMEDATA_RESULT = 17,
    JOIN_ROOM = 12,
    LEAVE_ROOM = 13,
    CONNECTED = 14,
    DISCONNECTED = 15,
    ROOM_DISMISS = 16,
    UPDATE_CUSTOM_STAYUS = 18,
    UPDATE_CUSTOM_PROPS = 19,
    UPDATE_ROOM_PROPS = 20,
    INSTANT_MESSAGE = 22,
    INSTANT_MESSAGE_ACK = 23
}

/**
 * 基于「线性同余」的伪随机数生成器
 * @public
 */
export declare class RandomUtils {
    private mask;
    private m;
    private a;
    private seed;
    constructor(seed: number);
    getNumber(): number;
}

/**
 * 帧广播消息
 * @public
 */
export declare interface RecvFrameMessage extends ServerFrameMessage {
    isReplay: boolean;
    time: number;
}

/**
 * 房间消息广播回调参数
 * @param roomId - 房间ID
 * @param sendPlayerId - 发送者playerId
 * @param msg - 消息内容
 * @public
 */
export declare interface RecvFromClientInfo {
    roomId: string;
    sendPlayerId: string;
    msg: string;
}

/**
 * 房间类
 * @public
 */
export declare class Room extends Base {
    static wsHeartBeatCycle: number;
    static autoFrame: AutoFrame;
    onConnect: {
        (this: any, cb: (player: FramePlayerInfo) => any): EventEmitter<(player: FramePlayerInfo) => any>;
        emit(player: FramePlayerInfo): void;
        off(cb: (player: FramePlayerInfo) => any): void;
        clear(): void;
    };
    onJoin: {
        (this: any, cb: (player: FramePlayerInfo) => any): EventEmitter<(player: FramePlayerInfo) => any>;
        emit(player: FramePlayerInfo): void;
        off(cb: (player: FramePlayerInfo) => any): void;
        clear(): void;
    };
    onLeave: {
        (this: any, cb: (player: FramePlayerInfo) => any): EventEmitter<(player: FramePlayerInfo) => any>;
        emit(player: FramePlayerInfo): void;
        off(cb: (player: FramePlayerInfo) => any): void;
        clear(): void;
    };
    onDismiss: {
        (this: any, cb: () => any): EventEmitter<() => any>;
        emit(): void;
        off(cb: () => any): void;
        clear(): void;
    };
    onDisconnect: {
        (this: any, cb: (player: FramePlayerInfo, event?: CloseEvent) => any): EventEmitter<(player: FramePlayerInfo, event?: CloseEvent) => any>;
        emit(player: FramePlayerInfo, event?: CloseEvent | undefined): void;
        off(cb: (player: FramePlayerInfo, event?: CloseEvent) => any): void;
        clear(): void;
    };
    onStartFrameSync: {
        (this: any, cb: () => any): EventEmitter<() => any>;
        emit(): void;
        off(cb: () => any): void;
        clear(): void;
    };
    onStopFrameSync: {
        (this: any, cb: () => any): EventEmitter<() => any>;
        emit(): void;
        off(cb: () => any): void;
        clear(): void;
    };
    onRecvFrame: {
        (this: any, cb: (msg: RecvFrameMessage | RecvFrameMessage[]) => any): EventEmitter<(msg: RecvFrameMessage | RecvFrameMessage[]) => any>;
        emit(msg: RecvFrameMessage | RecvFrameMessage[]): void;
        off(cb: (msg: RecvFrameMessage | RecvFrameMessage[]) => any): void;
        clear(): void;
    };
    onRequestFrameError: {
        (this: any, cb: (error: GOBEError) => any): EventEmitter<(error: GOBEError) => any>;
        emit(error: GOBEError): void;
        off(cb: (error: GOBEError) => any): void;
        clear(): void;
    };
    onUpdateCustomStatus: {
        (this: any, cb: (player: UpdateCustomStatusResponse) => any): EventEmitter<(player: UpdateCustomStatusResponse) => any>;
        emit(player: UpdateCustomStatusResponse): void;
        off(cb: (player: UpdateCustomStatusResponse) => any): void;
        clear(): void;
    };
    onUpdateCustomProperties: {
        (this: any, cb: (player: UpdateCustomPropertiesResponse) => any): EventEmitter<(player: UpdateCustomPropertiesResponse) => any>;
        emit(player: UpdateCustomPropertiesResponse): void;
        off(cb: (player: UpdateCustomPropertiesResponse) => any): void;
        clear(): void;
    };
    onRoomPropertiesChange: {
        (this: any, cb: (roomInfo: RoomInfo) => any): EventEmitter<(roomInfo: RoomInfo) => any>;
        emit(roomInfo: RoomInfo): void;
        off(cb: (roomInfo: RoomInfo) => any): void;
        clear(): void;
    };
    onRecvFromClient: {
        (this: any, cb: (recvFromClientInfo: RecvFromClientInfo) => any): EventEmitter<(recvFromClientInfo: RecvFromClientInfo) => any>;
        emit(recvFromClientInfo: RecvFromClientInfo): void;
        off(cb: (recvFromClientInfo: RecvFromClientInfo) => any): void;
        clear(): void;
    };
    onSendToClientFailed: {
        (this: any, cb: (error: GOBEError) => any): EventEmitter<(error: GOBEError) => any>;
        emit(error: GOBEError): void;
        off(cb: (error: GOBEError) => any): void;
        clear(): void;
    };
    private connection;
    private config;
    private frameId;
    private readonly frameRequestMaxSize;
    private frameRequesting;
    private frameRequestSize;
    private frameRequestTimes;
    private frameRequestList;
    private autoFrameRequesting;
    private autoRequestFrameFailed;
    private autoFrameRequestCacheList;
    private endpoint;
    private wsHeartbeatTimer;
    private _isSyncing;
    private _player;
    private _client;
    private initiativeLeaveRoomFlag;
    /**
     * 房间 ID
     */
    get id(): string;
    /**
     * 房间类型
     */
    get roomType(): string;
    /**
     * 房间名称
     */
    get roomName(): string;
    /**
     * 房间的短码
     */
    get roomCode(): string;
    /**
     * 房间自定义属性
     */
    get customRoomProperties(): string;
    /**
     * 房主 ID
     */
    get ownerId(): string;
    /**
     * 房间最大人数
     */
    get maxPlayers(): number;
    /**
     * 房间玩家列表
     */
    get players(): PlayerInfo[];
    /**
     * 路由信息
     */
    get router(): RouterInfo;
    /**
     * 0：公开房间，1：私有房间
     */
    get isPrivate(): number;
    /**
     * 创建时间
     */
    get createTime(): number;
    /**
     * 玩家自己
     */
    get player(): Player;
    /**
     * 房间是否处于帧同步
     */
    get isSyncing(): boolean;
    /**
     * 房间
     * @param config - 创建客户端参数
     */
    constructor(client: Client, config: RoomInfo);
    /**
     * websocket 建链
     * @param endpoint - 接入地址
     */
    connect(routerAddr: string, ticket: string): void;
    /**
     * 发送帧数据
     * @param frameData - 帧数据
     */
    sendFrame(frameData: string | string[]): void;
    /**
     * 请求补帧
     * @param beginFrameId - 起始帧号
     * @param size - 请求帧号
     */
    requestFrame(beginFrameId: number, size: number): void;
    /**
     * 重置房间起始帧ID，调用场景：在加入房间后，开始游戏前调用
     * @param frameId - 帧Id
     */
    resetRoomFrameId(frameId: number): void;
    /**
     * 移除所有事件监听
     */
    removeAllListeners(): void;
    /**
     * 重连
     */
    reconnect(playerConfig?: PlayerConfig): Promise<void>;
    /**
     * 开始帧同步
     */
    startFrameSync(): Promise<void>;
    /**
     * 结束帧同步
     */
    stopFrameSync(): Promise<void>;
    /**
     * 玩家房间信息查询
     */
    update(): Promise<Room>;
    /**
     * 更新房间自定义属性
     * @param updateRoomInfo - 需要更新的房间信息
     * @remark 只有房主才能更新房间信息
     */
    updateRoomProperties(updateRoomInfo: UpdateRoomInfo): Promise<Room>;
    /**
     * 离开房间
     */
    leave(): Promise<void>;
    /**
     * 解散房间
     * @remarks 房主才能解散房间
     */
    dismiss(): Promise<void>;
    /**
     * 移除房间内玩家
     * @param playerId - 被移除出的玩家ID
     * @remarks 只有房主有权限移除其他玩家
     * @remarks 房间在帧同步中，不能移除其他玩家
     */
    removePlayer(playerId: string): Promise<void>;
    /**
     * 发送消息给房间内玩家
     * @param sendToClientInfo - 发送房间内消息参数
     */
    sendToClient(sendToClientInfo: SendToClientInfo): Promise<void>;
    private onMessageCallback;
    private clearRequestFrame;
    private startWSHeartbeat;
    private doWSHeartbeat;
    stopWSHeartbeat(): void;
    private buildEndpoint;
    private checkInSync;
    private checkNotInSync;
    private checkNotInRequesting;
    private broadcastFramedata;
    private handleFramedataResult;
    private connectedMsgACKHandle;
    private leaveRoomMsgHandle;
    private joinRoomMsgHandle;
    disconnectMsgHandle(player: ServerFramePlayerInfo): void;
    private dismissRoomMsgHandle;
    private updateCustomStatusHandle;
    private instantMsgACKHandle;
    private queryFrameDataAckHandle;
}

/**
 * 房间信息
 * @public
 * @param appId - 游戏ID
 * @param roomId - 房间ID
 * @param roomType - 房间类型
 * @param roomCode - 房间的短码
 * @param roomName - 房间名称
 * @param roomStatus - 房间状态 0：空闲，1：帧同步中
 * @param customRoomProperties - 房间自定义属性
 * @param ownerId - 房主ID
 * @param maxPlayers - 房间最大支持人数
 * @param players - 房间内玩家
 * @param router - 路由信息
 * @param isPrivate - 是否私有
 * @param createTime - 创建时间
 */
export declare interface RoomInfo {
    appId: string;
    roomId: string;
    roomType: string;
    roomCode: string;
    roomName: string;
    roomStatus: number;
    customRoomProperties: string;
    ownerId: string;
    maxPlayers: number;
    players: PlayerInfo[];
    router: RouterInfo;
    isPrivate: number;
    createTime: number;
}

/**
 * 路由信息
 * @public
 */
export declare interface RouterInfo {
    routerId: number;
    routerType: number;
    routerAddr: string;
}

/**
 * 发送房间内消息参数
 * @param type - 房间状态 0：发送给房间内全部玩家，1：发送给房间内除本人外的其他所有玩家，2：发送给recvPlayerIdList的玩家
 * @param msg - 消息内容
 * @param recvPlayerList - 接收消息的玩家ID列表
 * @public
 */
export declare interface SendToClientInfo {
    type: number;
    msg: string;
    recvPlayerIdList?: string[];
}

/**
 * 服务端事件
 * @param eventType - 1：匹配开始；6：加入小队；7：离开小队；8：解散小队；9：更新小队；
 * @param eventParam - 事件相关信息
 * @public
 */
export declare interface ServerEvent {
    eventType: ServerEventCode;
    eventParam?: string;
}

/**
 * 服务端事件码
 * @public
 */
export declare const enum ServerEventCode {
    DEFAULT = 0,
    MATCH_START = 1,
    JOIN_GROUP = 6,
    LEAVE_GROUP = 7,
    DISMISS_GROUP = 8,
    UPDATE_GROUP = 9
}

/**
 * 服务端推送消息
 * @public
 */
export declare interface ServerFrameMessage {
    currentRoomFrameId: number;
    frameInfo: FrameInfo[];
    ext: FrameExtInfo;
}

/**
 * 服务端返回帧数据玩家信息
 */
declare interface ServerFramePlayerInfo extends FramePlayerInfo {
    extraInfo?: string;
}

/**
 * 初始化签名
 * @param sign - 签名
 * @param nonce - 随机正整数
 * @param timeStamp - 时间戳(秒)
 * @public
 */
export declare interface Signature {
    sign: string;
    nonce: string;
    timeStamp: number;
}

/**
 * SDK 状态码
 * @public
 */
export declare const enum StateCode {
    UNINITIALIZED = 0,
    INITIALIZED = 1,
    INROOM = 2,
    SYNCING = 3,
    ENTER_ROOM = 4,
    LEAVE_ROOM = 5,
    ENTER_SYNCING = 6,
    EXIT_SYNCING = 7
}

/**
 * 更新玩家属性响应信息
 * @public
 */
export declare interface UpdateCustomPropertiesResponse {
    playerId: string;
    customProperties: string;
}

/**
 * 更新玩家状态响应信息
 * @public
 */
export declare interface UpdateCustomStatusResponse {
    playerId: string;
    customStatus: number;
}

/**
 * 更新队伍信息参数
 * @param groupName - 队伍名称
 * @param ownerId - 队长ID
 * @param customGroupProperties - 队伍自定义属性
 * @param isLock - 是否禁止加入 0:不禁止 1:禁止 默认0
 * @public
 */
export declare interface UpdateGroupConfig {
    groupName?: string;
    ownerId?: string;
    customGroupProperties?: string;
    isLock?: number;
}

/**
 * 可以更新的房间信息属性
 * @public
 * @param roomName - 房间名称
 * @param customRoomProperties - 房间自定义属性
 * @param ownerId - 房主ID
 * @param isPrivate - 是否私有
 */
export declare interface UpdateRoomInfo {
    roomName?: string;
    customRoomProperties?: string;
    ownerId?: string;
    isPrivate?: number;
}

export { }
export as namespace GOBE
