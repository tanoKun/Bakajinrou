# 目次
<!-- TOC -->
* [目次](#目次)
* [コマンド一覧](#コマンド一覧)
  * [Map Setting](#map-setting)
    * [`/jobs`](#jobs)
    * [`/spectators set <target>`](#spectators-set-target)
    * [`/spectators remove <target>`](#spectators-remove-target)
    * [パーミッション](#パーミッション)
    * [`/map select <mapName>`](#map-select-mapname)
  * [Game Setting](#game-setting)
    * [`/jobs`](#jobs-1)
    * [`/spectators set <target>`](#spectators-set-target-1)
    * [`/spectators remove <target>`](#spectators-remove-target-1)
    * [`/map select <mapName>`](#map-select-mapname-1)
    * [パーミッション](#パーミッション-1)
  * [Infrastructure](#infrastructure)
    * [`/start`](#start)
    * [`/reset`](#reset)
    * [パーミッション](#パーミッション-2)
<!-- TOC -->

# コマンド一覧

## Map Setting

### `/jobs`
設定GUIを開き、役職分布を設定します。

### `/spectators set <target>`
指定したプレイヤーを観戦者にします。
- `target`: 観戦者ではないプレイヤー名

### `/spectators remove <target>`
指定した観戦者を参加者に戻します。
- `target`: 現在観戦者であるプレイヤー名

### パーミッション
- `bakajinrou.command.mapsetting`

### `/map select <mapName>`
指定されたマップをゲーム用に選択します。
- `mapName`: 登録済みのマップ名

## Game Setting

### `/jobs`
設定GUIを開き、役職分布を設定します。

### `/spectators set <target>`
指定したプレイヤーを観戦者にします。
- `target`: 観戦者ではないプレイヤー名
-
### `/spectators remove <target>`
指定した観戦者を参加者に戻します。
- `target`: 現在観戦者であるプレイヤー名
-
### `/map select <mapName>`
指定されたマップをゲーム用に選択します。
- `mapName`: 登録済みのマップ名
-
### パーミッション
- `bakajinrou.command.gamesetting`

## Infrastructure

### `/start`
ゲームを開始します。以下の条件を満たす必要があります。
- 適切な役職分布が設定されている
- 使用するマップが選択されている
-
### `/reset`
実行中のゲームを強制終了します。

### パーミッション
- `bakajinrou.command.gamesetting`計思想と、そのメリットを誰にでも完璧に伝えることができます。