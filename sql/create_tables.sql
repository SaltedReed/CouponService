-- 优惠券模板
create table if not exists t_coupon_template (
    id bigint not null auto_increment primary key,
    name varchar(20) not null,
    content varchar(100),
    scope_type int not null,   -- 应用范围类型，0：平台，1：店铺，2：商品
    scope bigint default null, -- 如果应用范围类型是店铺，该字段就是店铺ID
    user_type int not null,    -- 目标用户，0：所有用户，1：平台新用户，2：店铺新用户
    discount_type int not null,      -- 优惠类型，0：满减，1：满折
    discount_threshold int not null, -- 优惠门槛，即满多少元才能优惠
    discount_value float not null,   -- 优惠量，减多少元或打多少折，含义由优惠类型决定
    start_time datetime not null, -- 开始时间，包含该时间
    end_time datetime not null,   -- 结束时间，不包含该时间
    max_stock int not null, -- 最大库存
    stock int not null      -- 剩余库存
);

-- 优惠券适用的商品，当优惠券模板表的应用范围类型字段是商品时，应该查这个表
create table if not exists t_coupon_commodity_relation (
    id bigint not null auto_increment primary key,
    coupon_id bigint not null,
    commodity_id bigint not null
);

-- 优惠券实例
create table if not exists t_coupon_instance (
    id bigint not null auto_increment primary key,
    coupon_id bigint not null,
    user_id bigint not null,
    state int not null default 0, -- 优惠券状态，0：未使用，1：已使用，2：已过期
    receive_time datetime not null, -- 领取时间
    use_time datetime default null, -- 使用时间
    order_id bigint default null,   -- 使用该优惠券的订单的ID
    key idx_coupon_user(coupon_id, user_id)
);