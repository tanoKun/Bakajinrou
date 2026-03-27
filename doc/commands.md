# 目次

# MapSetting
マップの設定を追加、変更、削除します。

## コマンドの構文
### `/mapsetting create <mapName> <spawn> <lobby>`
新しいマップを作成します。
- `mapName`: 作成するマップの名前(文字列)
- `spawn`: スポーン地点のロケーション
- `lobby`: ロビー地点のロケーション

### `/mapsetting delete <mapName>`
指定されたマップを削除します。

### `/mapsetting update spawn <mapName> <spawn>`
指定マップのスポーン地点を更新します。

### `/mapsetting update lobby <mapName> <lobby>`
指定マップのロビー地点を更新します。

### `/mapsetting update time <mapName> <seconds>`
指定マップのゲーム開始後の制限時間(秒)を変更します。

### `/mapsetting update quartztime <mapName> <seconds>`
指定マップにおいて、ゲーム開始からクオーツを配布するまでの時間(秒)を設定します。

### `/mapsetting update icon <mapName> <material>`
指定マップのアイコンを更新します。

## パーミッション
- `bakajinrou.command.mapsetting`

# Prepare
ゲームの準備を行います。

## コマンドの構文
### `/prepare`
GUIの流れに沿って、ゲームの準備を行います。

## パーミッション
- `bakajinrou.command.prepare`