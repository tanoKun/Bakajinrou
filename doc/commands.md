# 目次

# MapSetting
マップの設定を追加、変更、削除します。

## コマンドの構文
### `/mapsetting create <mapName> <spawn> <lobby> <gimmick>`
新しいマップを作成します。
- `mapName`: 作成するマップの名前(文字列)
- `spawn`: スポーン地点のロケーション
- `lobby`: ロビー地点のロケーション
- `gimmick`: マップで使用するギミック。候補は下記の「ギミック一覧」を参照

### `/mapsetting delete <mapName>`
指定されたマップを削除します。

### `/mapsetting update spawn <mapName> <spawn>`
指定マップのスポーン地点を更新します。

### `/mapsetting update lobby <mapName> <lobby>`
指定マップのロビー地点を更新します。

### `/mapsetting update time <mapName> <seconds>`
指定マップのゲーム開始後の制限時間(秒)を変更します。

### `/mapsetting update icon <mapName> <material>`
指定マップのアイコンを更新します。

### `/mapsetting update gimmick <mapName> <gimmick>`
指定マップで使用するギミックを更新します。

## ギミック一覧

| 値 | ギミック |
| --- | --- |
| `none` | ギミックなし |
| `hijack` | 空港のハイジャック |
| `altar` | 森林公園の祭壇 |
| `overlook` | 城下町の展望台 |
| `suspicious_merchant` | 遺跡の怪しい村人 |
| `transfer_gate` | 時計塔の転送ゲート |

## ギミック用ArmorStand

座標を参照するギミックは、対象ワールドに配置済みのArmorStandのscoreboard tagを使用します。旧マップ互換の座標があるギミックは、対応するArmorStandがない場合のみ旧座標へフォールバックします。

| tag | 用途 |
| --- | --- |
| `jinrou_gimmick_altar` | 祭壇のディスペンサー位置 |
| `jinrou_gimmick_overlook_entry` | 展望台の入口 |
| `jinrou_gimmick_overlook_destination` | 展望台上の転送先 |
| `jinrou_gimmick_merchant` | 怪しい村人の召喚位置 |
| `4_tp` | 転送ゲート |
| `4_all` | 全役職が使用できる転送ゲート |
| `4_foxOnly` | 妖狐専用の転送ゲート |

## パーミッション
- `bakajinrou.command.mapsetting`

# Prepare
ゲームの準備を行います。

## コマンドの構文
### `/prepare`
GUIの流れに沿って、ゲームの準備を行います。

## パーミッション
- `bakajinrou.command.prepare`
