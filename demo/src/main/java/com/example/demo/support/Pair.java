package com.example.demo.support;

import java.io.Serializable;

import lombok.Data;

@Data(staticConstructor = "of")
public class Pair<S, T> implements Serializable {
    private static final long serialVersionUID = -5764604295409947478L;
    
    private final S first;
	private final T second;
}