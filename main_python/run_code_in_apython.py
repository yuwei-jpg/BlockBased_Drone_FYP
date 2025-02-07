import pexpect


def run_code_in_apython(code):
    try:
        # Start apython terminal
        apython_process = pexpect.spawn("apython")
        apython_process.expect(">>>")  # waiting for the prompts from terminal

        # Send the code in different lines
        for line in code.splitlines():
            apython_process.sendline(line)  # send the codes
            apython_process.expect(">>>")

            # Stay interact
        apython_process.interact()
    except Exception as e:
        print(f"Error: {e}")
