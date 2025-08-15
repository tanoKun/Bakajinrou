# 基本的なフロー
```mermaid
sequenceDiagram
    box UI層
        participant A as Adapters
        participant O as Observers
    end
    box APP層
        participant US as Usecase、Service
    end
    box Domain層
        participant V as Verification
        participant JG as JinrouGame
    end

    O ->> JG: 状態変更を購読

    A ->>+ US: 変換後、処理を委託
    US ->>+ V: 妥当性チェック
    V -->>- US: 妥当性
    US ->>+ JG: 状態変更
    JG ->>- JG: 状態変更の通知
```
**Adapters**とは、そのイベントの元となるリスナー(OnAttack、OnChatなど)です。
また、このクラスは**状態変更や妥当性のチェックは基本的に行いません**。それらを行うのは全てAPP層です。
**UI的副作用**を行うときは、**全て変更や通知を購読する**ことで得られる情報から行います。
基本的にUI的副作用は、APP、Domain層から直接呼び出されることはありません。

# 攻撃フロー

まず、全体のフロー図です。

```mermaid
sequenceDiagram
    box UI層
        participant CPE as ConsumedProtectionEffectors
        participant DO as DeathObservers
        participant AA as AttackAdapters
    end
    box APP層
        participant A as Attacking
    end
    box Domain層
        participant AV as AttackVerificator
        participant JG as JinrouGame
    end

    opt 購読
        CPE ->> AA: 攻撃の成功を条件に購読する
        DO ->> JG: 死亡を条件に購読する
    end

    AA ->>+ A: 攻撃を検知 
    A ->> AV: 攻撃の検証
    A ->>+ JG: 参加者の更新(手段剥奪、死亡状態)
    JG ->>- JG: 状態変更の通知
    A ->>- A: 攻撃の失敗、成功を通知
```

**攻撃の検知**はUIで行いますが、UIは変換以外の処理を行いません。
**妥当性、状態の変更**といったことはすべてAPP層に委託されます。



