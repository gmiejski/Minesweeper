package com.gmiejski.minesweeper.domain

import com.gmiejski.minesweeper.game.domain.GameView
import com.gmiejski.minesweeper.game.domain.grid.BOMB
import com.gmiejski.minesweeper.game.domain.grid.SAFE_FIELD
import com.gmiejski.minesweeper.game.domain.grid.UNKNOWN

fun drawGrid(grid: GameView) {
    DrawGrid().draw(grid)
}

class DrawGrid {
    fun draw(view: GameView) {
        view.view.gridRows.forEach {
            print("____".repeat(it.fields.size))
            println()
            it.fields.forEach {
                print("|")
                if (it.state == BOMB) {
                    print(" * ")
                } else if (it.state == SAFE_FIELD) {
                    print("   ")
                } else if (it.state == UNKNOWN) {
                    print("###")
                } else {
                    print("#${it.state}#")
                }
            }
            println()
            print("````".repeat(it.fields.size))
            println()
        }
    }
}