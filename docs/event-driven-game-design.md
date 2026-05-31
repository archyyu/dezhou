# Event-Driven Game Flow Design (Texas Holder)

## 1. Goal
Refactor game processing from direct method calls into an event-driven architecture, so game rules, state mutation, persistence, and notification are clearly separated and easier to extend.

Current issue in code:
- `PukerGame` mixes state, rules, timer logic, and notification.
- Commands call room methods directly and mutate state immediately.
- WebSocket notification is coupled into game domain flow.

Target result:
- Commands only produce `GameEvent`.
- A single room event loop applies events in order.
- Projections (WebSocket, DB logs, metrics) subscribe to committed events.

## 2. Design Principles
- Single writer per room: only one thread mutates one room state.
- Event first: every state change is represented by an event.
- Deterministic reduce: `GameState + Event -> New GameState`.
- Side effects after commit: broadcast/persist after event is applied.
- Idempotent handlers for external retries.

## 3. Proposed Package Structure

```text
com.archy.texasholder
  game
    api
      GameActionController.java          // receives player intent
      dto/
    application
      GameCommandService.java            // validates auth, routes to room processor
      RoomEventProcessor.java            // per-room event queue + loop
      EventDispatcher.java               // dispatch to handlers/projections
    domain
      model/
        GameAggregate.java               // aggregate root, reduce() entry
        GameState.java
        PlayerState.java
      event/
        GameEvent.java
        GameEventType.java
        events/
          PlayerJoinedEvent.java
          PlayerActionRequestedEvent.java
          BetPlacedEvent.java
          PlayerFoldedEvent.java
          RoundStartedEvent.java
          TurnStartedEvent.java
          TurnTimedOutEvent.java
          RoundEndedEvent.java
          GameEndedEvent.java
      command/
        PlayerActionCommand.java
      reducer/
        GameReducer.java                 // pure transition logic
      policy/
        TurnPolicy.java
        BlindPolicy.java
        SettlementPolicy.java
    infrastructure
      websocket
        GameStateProjection.java         // sends /topic/game.{roomId}.state
        GameEventProjection.java         // sends /topic/game.{roomId}.events
      persistence
        GameEventStore.java              // optional now, ready for later
        GameActionLogProjector.java
      scheduling
        RoomHeartbeatPublisher.java      // emits TickEvent each second
```

## 4. Event Model

### 4.1 Base Event
```java
public interface GameEvent {
    String eventId();
    int roomId();
    long sequence();
    Instant occurredAt();
    Integer actorUid();
    GameEventType type();
}
```

### 4.2 Event Categories
- Intent events: from API/WebSocket input.
- Domain events: validated state transitions.
- System events: timeout/tick/auto-start.
- Integration events: message prepared for external consumers.

### 4.3 Core Events (mapping from current methods)
- `GameStartedEvent` <- `gameStartHandle()`
- `BlindPostedEvent` <- `autoSetPlayerState()`
- `CardsDealtToPlayersEvent` <- `flopPlayersPukers()`
- `TurnStartedEvent` <- `settleNextTurnPlayer()`
- `PlayerCheckedEvent` <- `playerCheck()`
- `PlayerCalledEvent` <- `playerFollow()`
- `PlayerRaisedEvent` <- `playerAddBet()` / `playerRaise()`
- `PlayerAllInEvent` <- `playerAllIn()`
- `PlayerFoldedEvent` <- `playerDropCard()`
- `RoundAdvancedEvent` <- `roundOverHandle()`
- `CommunityCardsRevealedEvent` <- `flop3Pukers/turnPuker/riverPuker`
- `GameSettledEvent` <- `balanceBet()`
- `GameEndedEvent` <- `gameOverHandle()`
- `TurnTimedOutEvent` <- `checkTheCurrentPlayer()`

## 5. Processing Flow

1. Client sends action (`/api/v1/game/{roomId}/action/{cmd}`).
2. Controller builds `PlayerActionCommand`.
3. `GameCommandService` converts command to `PlayerActionRequestedEvent` and pushes to `RoomEventProcessor` queue.
4. Room loop reads one event at a time.
5. `GameReducer` validates and emits one or more domain events.
6. Aggregate applies events and updates in-memory state.
7. `EventDispatcher` notifies projections:
   - `GameStateProjection` (full state)
   - `GameEventProjection` (delta event)
   - persistence projector (`game_action` etc.)

## 6. Room Event Loop (Single Writer)

Per room create a processor:
- `BlockingQueue<GameEvent> queue`
- dedicated worker (`ExecutorService` single thread)
- monotonically increasing `sequence`

Pseudo:
```java
while (running) {
  GameEvent event = queue.take();
  List<GameEvent> newEvents = reducer.decide(currentState, event);
  for (GameEvent e : newEvents) {
    currentState = reducer.apply(currentState, e);
    dispatcher.afterCommit(e, currentState);
  }
}
```

This replaces direct multi-location mutation and avoids race conditions across REST, WS, and heartbeat.

## 7. Timer/Heartbeat Integration

Current `RoomDealUnit` should publish `TickEvent(roomId, now)` every second.
Reducer handles:
- auto game start when `>=2 players` and waiting timeout reached.
- turn timeout -> `TurnTimedOutEvent` -> fold/check policy.
- standup/leave expiration checks.

No direct mutation inside scheduler.

## 8. WebSocket Contract

Keep both topics:
- `/topic/game.{roomId}.events`: event deltas (lightweight).
- `/topic/game.{roomId}.state`: authoritative state snapshot.

Recommendation:
- On each committed domain event, send delta.
- On round/game boundary, also send full state.
- Include `sequence` in messages so clients can detect gaps and request resync.

## 9. Migration Plan (Low Risk)

### Phase 1: Introduce event skeleton
- Add `GameEvent`, `GameReducer`, `RoomEventProcessor`.
- Keep existing `PukerGame` methods, but call through processor for 1-2 commands (`follow`, `drop`) first.

### Phase 2: Move actions to reducer
- Port `playerLookCard`, `playerFollowBet`, `playerDropCard`, `playerAllIn`.
- Make `GameApiController` only publish intents.

### Phase 3: Move round/game transitions
- Port `turnOverHandle`, `roundOverHandle`, `gameOverHandle`, `gameStartHandle`.
- Convert heartbeat checks to `TickEvent` flow.

### Phase 4: Separate projections
- Move all `webSocketService.send...` out of `PukerGame` into projectors.
- Add optional DB event log projector.

### Phase 5: Clean old API
- Delete direct mutation methods from `PukerGame`.
- Keep only aggregate + reducer entry points.

## 10. Minimal First Iteration (Recommended)
Implement first with only these events:
- `PlayerActionRequestedEvent`
- `PlayerCalledEvent`
- `PlayerFoldedEvent`
- `TurnStartedEvent`
- `TurnTimedOutEvent`

This gives immediate value (ordering + decoupled notifications) without full rewrite.

## 11. Practical Notes for This Repo
- Reuse existing `GameCommandFactory` as intent parser, not executor.
- Keep `RoomService.roomsMap` as in-memory aggregate store for now.
- `WebSocketService` remains transport-only; remove rule decisions from it.
- Keep current REST routes to avoid frontend breaking changes.

## 12. Definition of Done
- Any player action is represented by stored/dispatchable event.
- Only room event loop mutates room state.
- WebSocket sends ordered events with sequence.
- Heartbeat publishes events, does not mutate state directly.
- Existing game rules pass regression tests for: follow/check/raise/fold/all-in, round switch, settlement.
