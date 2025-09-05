<script>
	import { goto } from '$app/navigation';
	import { page } from '$app/stores';
  import NotificationDisplay from '@beyonk/svelte-notifications/src/Notifications.svelte'
  import * as notifier from '@beyonk/svelte-notifications/src/notifier.js'
  import { user, adminState } from '$lib/store.js'
  import Switch from '$lib/components/Switch.svelte'

	$: segment = $page.route.id?.split('/')[1];

	const home = async () => {
    goto('/#');
  }

	const logout = async () => {
		const response = await fetch( '/api/logout', {
			method: "post",
			credentials: 'include',
			headers: {
				'Accept': 'application/json'
			}
		});
		notifier.success('User ' + ($user?.name || 'Unknown') + ' logged out.');
		user.set(null);
		goto('/');
	}
</script>

<div class="banner">
  <div class=branding>
    <div class=logo on:click={home}><img src=/WCFC-logo.jpg alt="WCFC Groundschool"></div>
    <div class=title>WCFC Ground School Materials</div>
  </div>
	{#if $user &&  ! $user.anonymous && $user.admin }
	  <div class="switch">
	    <Switch bind:value={$adminState} label="Admin Mode" design="inner"/>
		<div class="right"><a href="/api/logout">Logout</a></div>
	  </div>
	{/if}
</div>
<div class="nav">
	<div class="left"><a class:selected='{segment === undefined}' href='.'>home</a></div>
  <div class="left"><a class:selected='{segment === "private"}' href='private'>private</a></div>
  <div class="left"><a class:selected='{segment === "instrument"}' href='instrument'>instrument</a></div>
  <div class="left"><a class:selected='{segment === "commercial"}' href='commercial'>commercial</a></div>
  <div class="left"><a class:selected='{segment === "contact"}' href='contact'>contact</a></div>
	{#if $user &&  ! $user.anonymous && $user.admin }
		<div class="left"><a class:selected='{segment === "students"}' href='students'>students</a></div>
	{/if}
  <div class="left"><a class:selected='{segment === "about"}' href='about'>about</a></div>
  {#if $user &&  ! $user.anonymous}
    <div class="right">
      <div class="user">
		<span>{$user.name}</span>
      </div>
    </div>
  {/if}
  <NotificationDisplay />
</div>

<style>
  .logo {
    float:left;
    padding:10px;
    padding-bottom: 0px;
    cursor: pointer;
  }
  .title {
    float:right;
    margin-top:10px;
  }
	.nav {
		border-bottom: 2px solid rgba(40, 90, 149, 0.2);
		font-weight: 300;
		padding: 0 1em;
		height:100%;
		margin: 0;
		background: #ffffff;
		font-size: 12pt;
	}
  .banner {
    display:inline-block;
    vertical-align: middle;
    font-size: 2em;
    font-weight: 300;
    text-align: top;
    width: 100%;
  }
	/* clearfix */
	.nav::after {
		content: '';
		display: block;
		clear: both;
	}
	.left {
		display: block;
		float: left;
		color: #114444;
		text-decoration: none;
	}
  .right {
    display: block;
    float: right;
    color: #114444;
    text-decoration: none;
  }
  .switch {
    display: block;
    float: right;
    color: #114444;
    text-decoration: none;
    padding: 5px;
    font-size: 12px;
  }
  .user {
    padding: 1em 0.5em;
  }
	.selected {
		position: relative;
		display: inline-block;
	}
	.selected::after {
		position: absolute;
		content: '';
		width: calc(100% - 1em);
		height: 2px;
		background-color: rgba(40, 90, 149, 1);
		display: block;
		bottom: -1px;
	}
  .branding {
    display: inline-block;
  }
	a {
		text-decoration: none;
		padding: 1em 0.5em;
		display: block;
	}
</style>
