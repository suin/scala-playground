// ホテルの予約を例に、DDDでImmutableなEntityの実装サンプル

package playground.ddd

import org.joda.time.DateTime

object ImmutableEntities extends App {
  // 目次
  StateSourcingEntitiesExample()
  EventSourcingEntitiesExample()

  /**
   * ステートソーシングの実装サンプル
   */
  object StateSourcingEntitiesExample {
    // Value Objects
    case class ReservationId(id: Int) extends AnyVal
    case class RoomId(id: Int) extends AnyVal
    case class GuestId(id: Int) extends AnyVal
    sealed trait ReservationState
    object ReservationState {
      case object Scheduled extends ReservationState
      case class Staying(checkInDate: DateTime) extends ReservationState
      case class CheckedOut(checkInDate: DateTime, checkOutDate: DateTime) extends ReservationState
      case class Canceled(canceledOn: DateTime) extends ReservationState
    }

    // Entity
    object Reservation {
      // ケースクラスなので`val r = Reservation(...)`でも初期化できるが、
      // ユビキタスランゲージに合わせて、明示的に`reserve`メソッドを追加している
      def reserve(
        reservationId: ReservationId,
        roomId: RoomId,
        guestId: GuestId,
        expectedCheckInDate: DateTime,
        expectedCheckOutDate: DateTime
      ): Reservation = Reservation(
        reservationId,
        roomId,
        guestId,
        expectedCheckInDate,
        expectedCheckOutDate,
        ReservationState.Scheduled
      )
    }
    case class Reservation(
        reservationId: ReservationId,
        roomId: RoomId,
        guestId: GuestId,
        expectedCheckInDate: DateTime,
        expectedCheckOutDate: DateTime,
        state: ReservationState
    ) {
      def checkIn(checkInDate: DateTime): Reservation = state match {
        case ReservationState.Scheduled =>
          copy(state = ReservationState.Staying(checkInDate))
        case _ =>
          throw new AssertionError("Invalid state")
      }

      def checkOut(checkOutDate: DateTime): Reservation = state match {
        case ReservationState.Staying(checkInDate) =>
          copy(state = ReservationState.CheckedOut(checkInDate, checkOutDate))
        case _ =>
          throw new AssertionError("Invalid state")
      }

      def cancel(canceledOn: DateTime): Reservation = state match {
        case ReservationState.Scheduled =>
          copy(state = ReservationState.Canceled(canceledOn))
        case _ =>
          throw new AssertionError("Invalid state")
      }
    }

    def apply() = {
      val reservation1 = Reservation.reserve(ReservationId(1), RoomId(1101), GuestId(432), new DateTime("2016-02-01"), new DateTime("2016-02-03"))
      val reservation2 = reservation1.checkIn(new DateTime("2016-02-01T16:00:00"))
      val reservation3 = reservation2.checkOut(new DateTime("2016-02-03T11:30:00"))
      println("StateSourcingEntitiesExample")
      println(s"reservation1 = $reservation1")
      println(s"reservation2 = $reservation2")
      println(s"reservation3 = $reservation3")

      // 出力結果
      // reservation1 = Reservation(ReservationId(1),RoomId(1101),GuestId(432),2016-02-01T00:00:00.000+09:00,2016-02-03T00:00:00.000+09:00,Scheduled)
      // reservation2 = Reservation(ReservationId(1),RoomId(1101),GuestId(432),2016-02-01T00:00:00.000+09:00,2016-02-03T00:00:00.000+09:00,Staying(2016-02-01T16:00:00.000+09:00))
      // reservation3 = Reservation(ReservationId(1),RoomId(1101),GuestId(432),2016-02-01T00:00:00.000+09:00,2016-02-03T00:00:00.000+09:00,CheckedOut(2016-02-01T16:00:00.000+09:00,2016-02-03T11:30:00.000+09:00))
    }
  }

  /**
   * イベントソーシングの実装サンプル
   */
  object EventSourcingEntitiesExample {
    // Value Objects
    case class ReservationId(id: Int) extends AnyVal
    case class RoomId(id: Int) extends AnyVal
    case class GuestId(id: Int) extends AnyVal
    sealed trait ReservationState
    object ReservationState {
      // ステートソーシングと違い、チェックイン日等はCQRSのQuery Modelとして表現するため不要
      case object Scheduled extends ReservationState
      case object Staying extends ReservationState
      case object CheckedOut extends ReservationState
      case object Canceled extends ReservationState
    }

    // Domain Events
    sealed trait ReservationEvent
    case class RoomReserved(reservationId: ReservationId, roomId: RoomId, guestId: GuestId, expectedCheckInDate: DateTime, expectedCheckOutDate: DateTime, occurredOn: DateTime) extends ReservationEvent
    case class GuestCheckedIn(reservationId: ReservationId, guestId: GuestId, roomId: RoomId, occurredOn: DateTime) extends ReservationEvent
    case class GuestCheckedOut(reservationId: ReservationId, guestId: GuestId, roomId: RoomId, occurredOn: DateTime) extends ReservationEvent
    case class ReservationCanceled(reservationId: ReservationId, roomId: RoomId, occurredOn: DateTime) extends ReservationEvent

    // Entity
    object Reservation {
      def reserve(
        reservationId: ReservationId,
        roomId: RoomId,
        guestId: GuestId,
        expectedCheckInDate: DateTime,
        expectedCheckOutDate: DateTime
      ): (Reservation, ReservationEvent) =
        new Reservation() & RoomReserved(reservationId, roomId, guestId, expectedCheckInDate, expectedCheckOutDate, DateTime.now)
    }
    case class Reservation(
        reservationId: ReservationId,
        roomId: RoomId,
        guestId: GuestId,
        expectedCheckInDate: DateTime,
        expectedCheckOutDate: DateTime,
        state: ReservationState
    ) {
      def this() = this(
        ReservationId(0),
        RoomId(0),
        GuestId(0),
        DateTime.now,
        DateTime.now,
        ReservationState.Scheduled
      )

      def checkIn(checkInDate: DateTime): (Reservation, ReservationEvent) = {
        assert(state == ReservationState.Scheduled)
        this & GuestCheckedIn(reservationId, guestId, roomId, DateTime.now)
      }

      def checkOut(checkOutDate: DateTime): (Reservation, ReservationEvent) = {
        assert(state == ReservationState.Staying)
        this & GuestCheckedOut(reservationId, guestId, roomId, DateTime.now)
      }

      def cancel(canceledOn: DateTime): (Reservation, ReservationEvent) = {
        assert(state == ReservationState.Scheduled)
        this & ReservationCanceled(reservationId, roomId, DateTime.now)
      }

      // このメソッドは永続化されたイベントストリームからEntityを再構築するときに使う
      def +(event: ReservationEvent): Reservation = event match {
        case RoomReserved(reservationId, roomId, guestId, checkInDate, checkOutDate, _) =>
          copy(reservationId, roomId, guestId, checkInDate, checkOutDate, ReservationState.Scheduled)
        case e: GuestCheckedIn =>
          copy(state = ReservationState.Staying)
        case e: GuestCheckedOut =>
          copy(state = ReservationState.CheckedOut)
        case e: ReservationCanceled =>
          copy(state = ReservationState.Canceled)
      }

      def &(event: ReservationEvent): (Reservation, ReservationEvent) = (this + event, event)
    }

    def apply() = {
      val (reservation1, event1) = Reservation.reserve(ReservationId(1), RoomId(1101), GuestId(432), new DateTime("2016-02-01"), new DateTime("2016-02-03"))
      val (reservation2, event2) = reservation1.checkIn(new DateTime("2016-02-01T16:00:00"))
      val (reservation3, event3) = reservation2.checkOut(new DateTime("2016-02-03T11:30:00"))
      println("EventSourcingEntitiesExample")
      println(s"reservation1 = $reservation1")
      println(s"reservation2 = $reservation2")
      println(s"reservation3 = $reservation3")
      println(s"event1 = $event1")
      println(s"event2 = $event2")
      println(s"event3 = $event3")

      // 出力結果例
      // reservation1 = Reservation(ReservationId(1),RoomId(1101),GuestId(432),2016-02-01T00:00:00.000+09:00,2016-02-03T00:00:00.000+09:00,Scheduled)
      // reservation2 = Reservation(ReservationId(1),RoomId(1101),GuestId(432),2016-02-01T00:00:00.000+09:00,2016-02-03T00:00:00.000+09:00,Staying)
      // reservation3 = Reservation(ReservationId(1),RoomId(1101),GuestId(432),2016-02-01T00:00:00.000+09:00,2016-02-03T00:00:00.000+09:00,CheckedOut)
      // event1 = RoomReserved(ReservationId(1),RoomId(1101),GuestId(432),2016-02-01T00:00:00.000+09:00,2016-02-03T00:00:00.000+09:00,2016-01-13T09:55:56.676+09:00)
      // event2 = GuestCheckedIn(ReservationId(1),GuestId(432),RoomId(1101),2016-01-13T09:55:56.676+09:00)
      // event3 = GuestCheckedOut(ReservationId(1),GuestId(432),RoomId(1101),2016-01-13T09:55:56.677+09:00)
    }
  }
}
