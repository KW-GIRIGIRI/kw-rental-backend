from datetime import date, datetime, timedelta
from faker import Faker

from dataModel import *

fake = Faker("ko_KR")


def deleteOf(*models: Model):
    for model in models:
        model.delete().execute()


def insertMember(count):
    memberArgs = []
    for i in range(1, count + 1):
        memberArg = (i, '19970309', fake.email(), str(i), fake.name(), str(i), fake.phone_number(), 'USER')
        memberArgs.append(memberArg)
    for batch in chunked(memberArgs, 1000):
        Member.insert_many(batch, fields=[Member.id, Member.birth_date, Member.email, Member.member_number, Member.name,
                                          Member.password, Member.phone_number, Member.role]).execute()


def insertEquipmentAndItem(count):
    equipmentArgs = []
    itemArgs = []
    for i in range(1, count + 1):
        equipmentArgs.append((i, 'equipment', 1, fake.name() + str(i), 1, 1, 'CAMEMRA', 'components', 'description',
                              fake.url(), fake.company(), 'purpose', '한울관', None, None, 'notice', None))
        itemArgs.append((i, True, i, str(i), None))
    for batch in chunked(equipmentArgs, 1000):
        Asset.insert_many(batch, fields=[Asset.id, Asset.dtype, Asset.max_rental_days, Asset.name, Asset.total_quantity,
                                         Asset.rentable_quantity, Asset.category, Asset.components, Asset.description,
                                         Asset.img_url, Asset.maker, Asset.purpose, Asset.rental_place,
                                         Asset.reservation_count_per_day, Asset.notice, Asset.deleted_at]).execute()
    for batch in chunked(itemArgs, 1000):
        Item.insert_many(batch, fields=[Item.id, Item.available, Item.asset_id, Item.property_number,
                                        Item.deleted_at]).execute()


def insertReservationAndRentalSpec(countPerDate, rentedDates: list[date], reservedDates: list[date]):
    reservationArgs = []
    reservationSpecArgs = []
    rentalSpecArgs = []
    id = 1
    for date in rentedDates:
        for i in range(1, countPerDate + 1):
            id = id + 1
            reservationArgs.append((id, datetime.combine(date, datetime.min.time()).timestamp(), fake.email(), id,
                                    fake.name(), fake.phone_number(), 'purpose', True))
            reservationSpecArgs.append((id, 1, date + timedelta(1), date, 'RETURNED', id, id))
            rentalSpecArgs.append((id, datetime.combine(date, datetime.min.time()).timestamp(), str(i), id, id,
                                   datetime.combine(date + timedelta(1), datetime.min.time()).timestamp(), 'RETURNED',
                                   'equipment'))
    for date in reservedDates:
        for i in range(1, countPerDate + 1):
            id = id + 1
            reservationArgs.append((id, datetime.combine(date, datetime.min.time()).timestamp(), fake.email(), id,
                                    fake.name(), fake.phone_number(), 'purpose', False))
            reservationSpecArgs.append((id, 1, date + timedelta(1), date, 'RESERVED', id, id))
    for batch in chunked(reservationArgs, 1000):
        Reservation.insert_many(batch, fields=[Reservation.id, Reservation.accept_date_time, Reservation.email,
                                               Reservation.member_id, Reservation.name, Reservation.phone_number,
                                               Reservation.purpose, Reservation.is_terminated]).execute()
    for batch in chunked(reservationSpecArgs, 1000):
        ReservationSpec.insert_many(batch,
                                    fields=[ReservationSpec.id, ReservationSpec.amount, ReservationSpec.rental_end_date,
                                            ReservationSpec.rental_start_date, ReservationSpec.status,
                                            ReservationSpec.asset_id, ReservationSpec.reservation_id]).execute()
    for batch in chunked(rentalSpecArgs, 1000):
        RentalSpec.insert_many(batch, fields=[RentalSpec.id, RentalSpec.accept_date_time, RentalSpec.property_number,
                                              RentalSpec.reservation_id, RentalSpec.reservation_spec_id,
                                              RentalSpec.return_date_time, RentalSpec.status,
                                              RentalSpec.dtype]).execute()


db = databaseConnect.getDb()
db.connect()
with db.atomic():
    deleteOf(Member, Asset, Item, Reservation, ReservationSpec, RentalSpec)
    insertMember(100000)
    insertEquipmentAndItem(500000)
    insertReservationAndRentalSpec(100000, rentedDates=[date(2023, 3, 6), date(2023, 3, 7), date(2023, 3, 8)],
                                   reservedDates=[date(2023, 3, 9), date(2023, 3, 13), date(2023, 3, 14)])
db.close()
