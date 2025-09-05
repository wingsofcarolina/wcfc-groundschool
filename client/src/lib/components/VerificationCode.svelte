<script>
    import { onMount } from "svelte";

    /** @type {number|number[]} */
    export let length
    /** @type {HTMLInputElement[]} */
    let els = []
    /** @type {(number|null)[]} */
    let values = []
    /** @type {string} */
    export let code = 'typing'

    onMount(async () => {
        var ele = document.getElementById("b-first-input");
        if (ele) ele.focus();
    });

    $: {
        (() => {
            if (values.length != length || values.includes(null)) return code = 'typing'
            let numCode = 0
            values.forEach((value, index) => {
                numCode += (value || 0) * (10 ** (length - index - 1))
            })
            code = numCode.toString().padStart(length, '0')
        })()
    }

    /**
     * @param {KeyboardEvent} e
     */
    function handleMoveAndBackspace(e) {
        let targetIndex = +(/** @type {HTMLInputElement} */(e.target).getAttribute('data-index') || '0')

        switch(e.key) {
            case 'ArrowRight': //ArrowRight
                e.preventDefault()
                els[min((Array.isArray(length) ? length.reduce((a, b) => a + b, 0) : length) - 1, targetIndex + 1)].focus()
                break
            case 'ArrowLeft': //ArrowLeft
                e.preventDefault()
                els[max(0, targetIndex - 1)].focus()
                break
            case 'Backspace': //Backspace
                e.preventDefault()

                // if curent cell is empty we want to backspace the previous cell
                if (!values[targetIndex] && values[targetIndex] != 0) {
                    els[max(0, targetIndex - 1)].focus()
                    values[targetIndex - 1] = null
                } else {
                    values[targetIndex] = null
                }
                break
        }
    }

    /**
     * @param {KeyboardEvent} e
     */
    function handleKey(e) {
        if (Number.isNaN(+e.key)) return
        let index = +(/** @type {HTMLInputElement} */(e.target).getAttribute('data-index') || '0')
        values[index] = +e.key
        els[min((Array.isArray(length) ? length.reduce((a, b) => a + b, 0) : length) - 1, index + 1)].focus()
    }

    /**
     * @param {ClipboardEvent} e
     */
    function handlePaste(e) {
        if (!e.clipboardData || Number.isNaN(+e.clipboardData.getData('text'))) return
        waterfall({target: /** @type {HTMLInputElement} */(e.target), arr: e.clipboardData.getData('text')})
    }

    /**
     * @param {{target: HTMLInputElement, arr: string}} data
     */
    function waterfall(data) {
        let [first, ...rest] = data.arr
        let index = +(data.target.getAttribute('data-index') || '0')
        values[index] = +first
        els[min((Array.isArray(length) ? length.reduce((a, b) => a + b, 0) : length) - 1, index + 1)].focus()
        if (index == (Array.isArray(length) ? length.reduce((a, b) => a + b, 0) : length) - 1 || rest.length === 0) return
        waterfall({target: els[index + 1], arr: rest.join('')})
    }

    /**
     * @param {number} length
     */
    function range(length) {
        let arr = []

        for (let i = 0; i < length; i++) {
            arr.push(i)
        }

        return arr
    }

    /**
     * @param {number} a
     * @param {number} b
     */
    function min(a, b) {
        if (a < b) return a
        return b
    }

    /**
     * @param {number} a
     * @param {number} b
     */
    function max(a, b) {
        if (a > b) return a
        return b
    }

    /**
     * @param {number} idx
     * @param {number[]} arr
     */
    function getTotalLength(idx, arr) {
        return arr.slice(0, idx).reduce((previousValue, currentValue) => previousValue + currentValue, 0)
    }
</script>

<section class="input-wrapper">
    {#if Array.isArray(length)}
        {#each length as part, idx}
            {#if idx != 0}
                <span>-</span>
            {/if}
            {#each range(part) as index}
                <input id="{index == 0 ? 'a-first-input' : ''}" type="number" on:keydown="{handleMoveAndBackspace}" on:keypress|preventDefault="{handleKey}" on:paste|preventDefault="{handlePaste}" bind:this="{els[index + getTotalLength(idx, length)]}" bind:value="{values[index + getTotalLength(idx,length)]}" data-index="{index + getTotalLength(idx, length)}">
            {/each}
        {/each}
    {:else}
        {#each range(length) as index}
            <input id="{index == 0 ? 'b-first-input' : ''}" type="number" on:keydown="{handleMoveAndBackspace}" on:keypress|preventDefault="{handleKey}" on:paste|preventDefault="{handlePaste}" bind:this="{els[index]}" bind:value="{values[index]}" data-index="{index}">
        {/each}
    {/if}
</section>

<style>
    /* removes up and down arrows from inputs */
    input::-webkit-outer-spin-button,
    input::-webkit-inner-spin-button {
        -webkit-appearance: none;
    }
    input[type=number] {
        -moz-appearance: textfield;
    }

    /* STYLING */
    input {
        font-size: 2rem;
        border-radius: 0.4rem;
        border: 2px solid #e5e5e5;
        outline: none;
        padding: 0.25rem 1rem;
        box-sizing: content-box;
        width: 1ch;
    }
    input:focus {
        border: 2px solid #5f91f0;
    }
    span {
        font-weight: bold;
    }
    .input-wrapper {
        display: flex;
        justify-content: space-between;
        align-items: center;
        width: 100%;
    }
</style>
