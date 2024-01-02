package com.bitian.common.util;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;
import java.util.function.Function;

/**
 * 处理函数的包装器 处理stream流中可能产生的异常*
 * @author hexuan*
 * @param <L>
 * @param <R>
 */
public class Either<L, R> {

    /**
     * 左-存储异常信息*
     */
    private final L left;

    /**
     * 右-存储正常的CheckedFunction处理结果*
     */
    private final R right;

    private Either(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <T, R> Function<T, Either> liftWithValue(LambdaUtil.CheckedFunction<T, R> function) {
        return t -> {
            try {
                return Either.Right(function.apply(t));
            } catch (Exception ex) {
                return Either.Left(Pair.of(ex, t));
            }
        };
    }

    public static <T, R> Function<T, Either> lift(LambdaUtil.CheckedFunction<T, R> function) {
        return t -> {
            try {
                return Either.Right(function.apply(t));
            } catch (Exception ex) {
                return Either.Left(ex);
            }
        };
    }

    public static <L, R> Either<L, R> Left(L value) {
        return new Either(value, null);
    }

    public static <L, R> Either<L, R> Right(R value) {
        return new Either(null, value);
    }

    public <T> Optional<T> mapLeft(Function<? super L, T> mapper) {
        if (isLeft()) {
            return Optional.of(mapper.apply(left));
        }
        return Optional.empty();
    }

    public <T> Optional<T> mapRight(Function<? super R, T> mapper) {
        if (isRight()) {
            return Optional.of(mapper.apply(right));
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        if (isLeft()) {
            return "Left(" + left + ")";
        }
        return "Right(" + right + ")";
    }

    public Optional<L> getLeft() {
        return Optional.ofNullable(left);
    }

    public Optional<R> getRight() {
        return Optional.ofNullable(right);
    }

    public boolean isLeft() {
        return left != null;
    }

    public boolean isRight() {
        return right != null;
    }
}
