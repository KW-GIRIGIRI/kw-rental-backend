from peewee import *

import databaseConnect

db = databaseConnect.getDb()


class BaseModel(Model):
    id = BigIntegerField(primary_key=True)

    class Meta:
        database = db


class Member(BaseModel):
    birth_date = CharField()
    email = CharField()
    member_number = CharField()
    name = CharField()
    phone_number = CharField()
    password = CharField()
    role = CharField()


class Asset(BaseModel):
    dtype = CharField()
    max_rental_days = IntegerField()
    name = CharField()
    total_quantity = IntegerField()
    rentable_quantity = IntegerField()
    category = CharField()
    components = CharField()
    description = CharField()
    img_url = CharField()
    maker = CharField()
    purpose = CharField()
    rental_place = CharField()
    is_available = BooleanField()
    reservation_count_per_day = IntegerField()
    notice = TextField()
    deletedAt = DateField()


class Item(BaseModel):
    available = BooleanField()
    asset_id = BigIntegerField()
    property_number = CharField()
    deleted_at = DateField()


class LabRoomDailyBan(BaseModel):
    ban_date = DateField()
    lab_room_id = BigIntegerField()


class Inventory(BaseModel):
    member_id = BigIntegerField()
    asset_id = BigIntegerField()
    amount = IntegerField()
    rental_end_date = DateField()
    rental_start_date = DateField()


class Penalty(BaseModel):
    member_id = BigIntegerField()
    end_date = DateField()
    start_date = DateField()
    reason = CharField()
    rental_spec_id = BigIntegerField()
    reservation_id = BigIntegerField()
    reservation_spec_id = BigIntegerField()


class Reservation(BaseModel):
    accept_date_time = TimestampField()
    email = CharField()
    member_id = BigIntegerField()
    name = CharField()
    phone_number = CharField()
    purpose = CharField()
    is_terminated = BooleanField()


class ReservationSpec(BaseModel):
    amount = IntegerField()
    rental_end_date = DateField()
    rental_start_date = DateField()
    status = CharField()
    asset_id = BigIntegerField()
    reservation_id = BigIntegerField()


class RentalSpec(BaseModel):
    accept_date_time = TimestampField()
    property_number = CharField()
    reservation_id = BigIntegerField()
    reservation_spec_id = BigIntegerField()
    return_date_time = TimestampField()
    status = CharField()
    dtype = CharField()
