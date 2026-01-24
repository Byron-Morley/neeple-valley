package com.liquidpixel.main.interfaces;

import com.badlogic.ashley.core.Component;

public interface DisposableComponent extends Component {
    void dispose();
}
